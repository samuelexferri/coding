import java.io.{File, PrintWriter}

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class Solver(var file: String) extends Thread {

  val _path: String = System.getProperty("user.dir") + "\\src"

  override def run(): Unit = {

    ////////////////////////////////// INPUT //////////////////////////////////

    println(s"$file : Solver started execution")

    val buffer = Source.fromFile(new File(_path + "\\input\\" + file))
    var lines = buffer.getLines.toList
    buffer.close

    val dimensions: List[Int] = lines(0).split("\\s").map(_.toInt).toList
    val width = dimensions(0)
    val height = dimensions(1)

    var mappa: ArrayBuffer[String] = new ArrayBuffer()
    var developers: ArrayBuffer[Developer] = ArrayBuffer[Developer]()
    var managers: ArrayBuffer[ProjectManager] = ArrayBuffer()

    var out_developers: ArrayBuffer[Developer] = new ArrayBuffer[Developer]()
    var out_managers: ArrayBuffer[ProjectManager] = new ArrayBuffer[ProjectManager]()

    lines = lines.tail
    var i = 0

    while (i < height) {
      mappa.addOne(lines(i))
      i += 1
    }

    val dev_num: Int = lines(i).toInt
    lines = lines.drop(i + 1)
    i = 0
    while (i < dev_num) {
      val line = lines(i).split("\\s")
      developers.addOne(new Developer(line(0), line(1).toInt, line.takeRight(2).toList, i, -1, -1, Int.MaxValue))
      i += 1
    }

    val pm_num: Int = lines(i).toInt
    lines = lines.drop(i + 1)
    i = 0
    while (i < pm_num) {
      val line = lines(i).split("\\s")
      managers.addOne(new ProjectManager(line(0), line(1).toInt, i, -1, -1))
      i += 1
    }


    ////////////////////////////////// ALGORITHM //////////////////////////////////

    def getSkills(x: Int, y: Int): List[String] = {
      val skills = ArrayBuffer[String]()
      if (x + 1 < width) out_developers.filter(dev => dev.x == x + 1 && dev.y == y).foreach(dev => skills.addAll(dev.skills))
      if (x - 1 > 0) out_developers.filter(dev => dev.x == x - 1 && dev.y == y).foreach(dev => skills.addAll(dev.skills))
      if (y + 1 < height) out_developers.filter(dev => dev.x == x && dev.y == y + 1).foreach(dev => skills.addAll(dev.skills))
      if (y - 1 > 0) out_developers.filter(dev => dev.x == x && dev.y == y - 1).foreach(dev => skills.addAll(dev.skills))
      skills.distinct.toList
    }

    def getCompanies(x: Int, y: Int): List[String] = {
      val companies = ArrayBuffer[String]()
      if (x + 1 < width) {
        out_developers.filter(dev => dev.x == x + 1 && dev.y == y).foreach(dev => companies.addOne(dev.company))
        out_managers.filter(dev => dev.x == x + 1 && dev.y == y).foreach(dev => companies.addOne(dev.company))
      }
      if (x - 1 > 0) {
        out_developers.filter(dev => dev.x == x - 1 && dev.y == y).foreach(dev => companies.addOne(dev.company))
        out_managers.filter(dev => dev.x == x - 1 && dev.y == y).foreach(dev => companies.addOne(dev.company))
      }
      if (y + 1 < height) {
        out_developers.filter(dev => dev.x == x && dev.y == y + 1).foreach(dev => companies.addOne(dev.company))
        out_managers.filter(dev => dev.x == x && dev.y == y + 1).foreach(dev => companies.addOne(dev.company))

      }
      if (y - 1 > 0) {
        out_developers.filter(dev => dev.x == x && dev.y == y - 1).foreach(dev => companies.addOne(dev.company))
        out_managers.filter(dev => dev.x == x && dev.y == y - 1).foreach(dev => companies.addOne(dev.company))
      }
      companies.distinct.toList
    }

    developers = developers.sortBy(_.company)
    managers = managers.sortBy(_.bonus)

    var riga = 0
    var colonna = 0

    while (riga < height) {
      colonna = 0

      while (colonna < width) {
        if (!mappa(riga)(colonna).equals('#')) {

          // Project Manager
          if (mappa(riga)(colonna).equals('M') && !managers.isEmpty) {

            var candidates: ArrayBuffer[ProjectManager] = ArrayBuffer()
            val companies = getCompanies(riga, colonna)

            companies.foreach(comp => candidates.addAll(managers.filter(dev => dev.company == comp)))

            var pm: ProjectManager = managers.head

            candidates = candidates.sortBy(x => Math.abs(x.bonus))(Ordering.Int.reverse)

            if (!candidates.isEmpty) pm = candidates(0)

            managers.remove(managers.indexWhere(_.position == pm.position))
            out_managers.addOne(new ProjectManager(pm.company, pm.bonus, pm.position, riga, colonna))
          }

          // Developer
          if (mappa(riga)(colonna).equals('_') && !developers.isEmpty) {
            var dev = developers.head

            val skills = getSkills(riga, colonna)
            val companies = getCompanies(riga, colonna)

            var candidates: ArrayBuffer[Developer] = ArrayBuffer()

            companies.foreach(comp => candidates.addAll(developers.filter(dev => dev.company == comp)))

            candidates.foreach(dev => {
              //dev.score = dev.skills.filter(skill => skills.contains(skill)).map(x => 1).sum - dev.skills.filter(skill => !skills.contains(skill)).map(x => 1).sum
              dev.score = dev.skills.filter(skill => skills.contains(skill)).map(x => 1).sum
            })

            // Score better low
            candidates = candidates.sortBy(x => Math.abs(x.score))(Ordering.Int.reverse)

            if (!candidates.isEmpty) dev = candidates(0)

            developers.remove(developers.indexWhere(_.position == dev.position))
            out_developers.addOne(new Developer(dev.company, dev.bonus, dev.skills, dev.position, riga, colonna, Int.MaxValue))
          }
        }

        colonna += 1
      }

      riga += 1
    }

    // Add the remain vacant dependents
    developers.foreach(dev => {
      out_developers.addOne(new Developer(dev.company, dev.bonus, dev.skills, dev.position, -1, -1, Int.MaxValue))
    })

    managers.foreach(pm => {
      out_managers.addOne(new ProjectManager(pm.company, pm.bonus, pm.position, -1, -1))
    })


    ////////////////////////////////// OUTPUT ////////////////////////////

    val writer = new PrintWriter(new File(_path + "\\output\\output_" + file))

    out_developers = out_developers.sortBy(_.position)
    for (a <- out_developers) {
      if (a.x == -1)
        writer.append('X' + "\n")
      else
        writer.append((a.y).toString + " " + (a.x).toString + "\n")
    }

    out_managers = out_managers.sortBy(_.position)
    for (a <- out_managers) {
      if (a.x == -1)
        writer.append('X' + "\n")
      else
        writer.append((a.y).toString + " " + (a.x).toString + "\n")
    }

    writer.close()

    println(s"$file : Solver execution finished")
  }
}