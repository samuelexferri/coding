import java.io.{File, PrintWriter}
import java.util.ArrayList

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

// TODO: Not work (D, E)
object Pratice {

  def main(args: Array[String]): Unit = {

    // FILE
    val file = "C:\\Users\\samuelexferri\\IdeaProjects\\Hash Code (2020)\\src\\d_quite_big.in"

    val buffer = Source.fromFile(file)
    val lines = buffer.getLines.toList
    buffer.close

    // VARIABLES
    val max = lines(0).split("\\s")
    val maxfette = max(0).toInt
    val maxtipi = max(1).toInt

    val index = List.range(0, maxtipi)
    val fette = lines(1).split("\\s")
    val doppia = index zip fette

    val limite_sassi = maxfette * 0.95
    var pezzi = limite_sassi

    var arr: ArrayList[Int] = new ArrayList()

    for (l <- doppia.reverse) {
      if (l._2.toInt < pezzi) {
        println(l._1)
        arr.add(l._1)
        pezzi = pezzi - l._2.toInt
      }
    }

    pezzi = pezzi + 0.05 * maxfette

    var break = 0
    var partenza = 0

    for (l <- doppia.reverse) {
      if (l._2.toInt < pezzi && break == 0) {
        partenza = l._1.toInt
        break = 1
      }
    }

    // TRIPLA
    var tripla: ArrayBuffer[(Int, Int, Int)] = new ArrayBuffer() // Tripla(sum, i1, i2)

    println(partenza)
    for (n <- List.range(0, partenza)) {
      for (m <- List.range(0, partenza)) {
        tripla.addOne((doppia(n)._2.toInt + doppia(m)._2.toInt, doppia(n)._1.toInt, doppia(m)._1.toInt))
      }
    }

    tripla = compressFunctional(tripla.toList).toArray.to(ArrayBuffer)

    // Delete consecutive duplicates
    def compressFunctional[A](ls: List[A]): List[A] =
      ls.foldRight(List[A]()) { (h, r) =>
        if (r.isEmpty || r.head != h) h :: r
        else r
      }

    tripla = tripla.sortBy(_._1.toInt)(Ordering.Int)

    for (l <- tripla.reverse) {
      if (l._1.toInt < pezzi) {
        println(l._1)
        arr.add(l._2)
        arr.add(l._3)
        pezzi = pezzi - l._1.toInt
      }
    }

    // OUTPUT
    val writer = new PrintWriter(new File("C:\\Users\\samuelexferri\\IdeaProjects\\Hash Code (2020)\\src\\output_e.txt"))
    writer.append(arr.size().toString + '\n')

    val it = arr.iterator()

    while (it.hasNext) {
      writer.append(it.next().toString + " ")
    }

    writer.close
  }
}