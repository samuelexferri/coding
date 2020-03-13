import java.io.{File, PrintWriter}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class Library(var ID: Int, var numbooks: Int, var books: ArrayBuffer[Book], var signupdays: Int, var booksxday: Int, var booksToSend: ArrayBuffer[Book], var differentBooks: ArrayBuffer[Book]) {
  override def toString: String = ID.toString + " - " + "NumBooks: " + numbooks + ", SignUp: " + signupdays + ", BxD: " + booksxday + ", DifferentBooks: " + differentBooks.size
}

class Book(var id: Int, var scores: Int) extends Ordered[Book] {
  def compare(that: Book): Int = this.scores - that.scores
}

object Main {

  def main(args: Array[String]): Unit = {

    val debugMode = true
    val analysisMode = false

    val path: String = "C:\\Users\\samuelexferri\\IdeaProjects\\Google Hash Code (2020)\\src\\"


    ////////////////////////////////// INPUT //////////////////////////////////

    val files: List[String] = List[String](
      "a_example.txt",
      "b_read_on.txt",
      "c_incunabula.txt",
      "d_tough_choices.txt",
      "e_so_many_books.txt",
      "f_libraries_of_the_world.txt"
    )

    files.foreach(file => {
      println("[STARTING PROBLEM " + file + "]")

      val startTime = System.nanoTime

      val lines: List[String] = Source.fromFile(new File(path + file)).getLines.toList
      val configuration: String = lines(0)
      val score: List[Int] = lines(1).split("\\s").toList.map(_.toInt)

      val numbooks = configuration.split("\\s")(0).toInt
      val numlibraries = configuration.split("\\s")(1).toInt
      val numdays = configuration.split("\\s")(2).toInt

      val libraries_info = lines.drop(2)

      var libraries: ArrayBuffer[Library] = ArrayBuffer[Library]()

      var i = 0
      var j = 0

      while (i < libraries_info.size - 1) {
        val params: List[String] = libraries_info(i).split("\\s").toList
        val lbooks: ArrayBuffer[Book] = libraries_info(i + 1).split("\\s").to(ArrayBuffer).map(x => new Book(x.toInt, score(x.toInt)))

        libraries.addOne(
          new Library(
            j,
            params(0).toInt,
            lbooks,
            params(1).toInt,
            params(2).toInt,
            ArrayBuffer[Book](),
            ArrayBuffer[Book](),
          )
        )

        j += 1
        i += 2
      }


      ////////////////////////////////// DATA ANALYSIS //////////////////////////////////

      if (debugMode) println("A - DATA ANALYSIS")

      var libNumBooks: ArrayBuffer[Int] = new ArrayBuffer()
      var libNumBxD: ArrayBuffer[Int] = new ArrayBuffer()
      var libNumDaySU: ArrayBuffer[Int] = new ArrayBuffer()

      if (analysisMode) {
        libNumBooks = (for (library <- libraries) yield library.numbooks)
        libNumBxD = (for (library <- libraries) yield library.booksxday)
        libNumDaySU = (for (library <- libraries) yield library.signupdays)

        println("NumBooks: \n AVG: " + libNumBooks.sum / libraries.size + "\n MAX: " + libNumBooks.max + "\n MIN: " + libNumBooks.min)
        println("BxD: \n AVG: " + libNumBxD.sum / libraries.size + "\n MAX: " + libNumBxD.max + "\n MIN: " + libNumBxD.min)
        println("SignUp: \n AVG: " + libNumDaySU.sum / libraries.size + "\n MAX: " + libNumDaySU.max + "\n MIN: " + libNumDaySU.min)
      }


      ////////////////////////////////// DATA PRE-PROCESSING (FILTER) ////////////////////////////

      if (debugMode) println("B - DATA PRE-PROCESSING (FILTER)")

      // Remove duplicates in the same library
      for (library <- libraries) {
        library.books = library.books.distinct
      }


      ////////////////////////////////// SCORE (MEAN, HEURISTIC) //////////////////////////////////

      if (debugMode) println("C - SCORE (MEAN, HEURISTIC)")

      var hashmapScoreMean: mutable.HashMap[Double, Double] = mutable.HashMap[Double, Double]()
      var hashmapScoreHeuristic: mutable.HashMap[Double, Double] = mutable.HashMap[Double, Double]()

      for (library <- libraries) {
        // Score (Total)
        var score_tot = 0
        for (book <- library.books)
          score_tot += score.apply(book.id)

        // Score (Mean)
        var score_mean = score_tot / library.numbooks

        hashmapScoreMean.addOne(library.ID, score_mean)

        var sq_diff: Double = 0.0
        for (book <- library.books)
          sq_diff = sq_diff + scala.math.pow((score.apply(book.id).toDouble - hashmapScoreMean.getOrElse(library.ID, 0.0)), 2)

        // Score (Variance)
        var score_var = sq_diff / library.numbooks

        if (score_var == 0)
          score_var = 0.0000000000000001 // Avoid zero division

        if (file.equals("a_example.txt") || file.equals("b_read_on.txt"))
          hashmapScoreHeuristic.addOne(library.ID, 0)

        if (file.equals("c_incunabula.txt"))
          hashmapScoreHeuristic.addOne(library.ID, scala.math.pow(score_tot, 2) / scala.math.pow(library.signupdays, 2))

        if (file.equals("d_tough_choices.txt"))
          hashmapScoreHeuristic.addOne(library.ID, scala.math.pow(score_tot, 1) / score_var)

        if (file.equals("e_so_many_books.txt"))
          hashmapScoreHeuristic.addOne(library.ID, scala.math.pow(score_tot, 2) * library.booksxday / (scala.math.pow(library.signupdays, 2) * library.numbooks * score_var))

        if (file.equals("f_libraries_of_the_world.txt"))
          hashmapScoreHeuristic.addOne(library.ID, scala.math.pow(score_tot, 2) / (scala.math.pow(library.signupdays, 2) * scala.math.sqrt(score_var)))
      }


      ////////////////////////////////// SORT LIBRARIES 1 //////////////////////////////////

      if (debugMode) println("D - SORT LIBRARIES 1")

      def sorter1(a: Library, b: Library): Boolean = {
        var boolean = true

        // Dataset A-B
        if (file.equals("a_example.txt") || file.equals("b_read_on.txt"))
          boolean = a.signupdays < b.signupdays

        // Dataset C-D-E-F
        if (file.equals("c_incunabula.txt") || file.equals("d_tough_choices.txt") || file.equals("e_so_many_books.txt") || file.equals("f_libraries_of_the_world.txt"))
          boolean = hashmapScoreHeuristic.getOrElse(a.ID, 0.0) > hashmapScoreHeuristic.getOrElse(b.ID, 0.0)

        boolean
      }

      libraries = libraries.sortWith(sorter1)

      // Analysis (Writer)
      val w = new PrintWriter(new File(path + "output\\analysis_" + file))
      libraries.foreach(l => w.append(l.toString + " ScoreHeuristic: " + hashmapScoreHeuristic.getOrElse(l.ID, 0.0) + "\n"))
      w.append("END")
      w.close()


      ////////////////////////////////// DIFFERENT BOOKS //////////////////////////////////

      if (debugMode) println("E - DIFFERENT BOOKS")

      var already_seen_books: ArrayBuffer[Int] = ArrayBuffer() // Performance: use "Int" instead of "Book"
      already_seen_books.clear()

      // Start from the first in the array!
      for (l <- libraries) {
        l.books.foreach(b => {
          if (!already_seen_books.contains(b.id)) {
            already_seen_books.addOne(b.id)
            l.differentBooks.addOne(b)
          }
        })
      }


      ////////////////////////////////// SORT BOOKS //////////////////////////////////

      if (debugMode) println("F - SORT BOOKS")

      for (library <- libraries) {
        library.books = library.books.sorted.reverse
        library.differentBooks = library.differentBooks.sorted.reverse
      }


      ////////////////////////////////// SCORE DIFFERENT (MEAN, HEURISTIC) //////////////////////////////////

      var hashmapScoreTotDiff: mutable.HashMap[Double, Double] = mutable.HashMap[Double, Double]()

      // Only who use it
      if (file.equals("c_incunabula.txt") || file.equals("d_tough_choices.txt")) {

        if (debugMode) println("G - SCORE DIFFERENT (MEAN)")

        for (library <- libraries) {
          // Score (Total)
          var score_tot = 0
          for (book <- library.differentBooks)
            score_tot += score.apply(book.id)

          hashmapScoreTotDiff.addOne(library.ID, score_tot)
        }
      }

      // Calculate different score based only on firsts (BxD * numdays)
      if (file.equals("e_so_many_books.txt")) {

        if (debugMode) println("G - SCORE DIFFERENT (MEAN)")

        // Different books sorted before (Reverse)

        for (library <- libraries) {
          var max = library.booksxday * numdays
          var count = 0

          // Score (Total)
          var score_tot = 0
          for (book <- library.books)
            if (count < max) {
              score_tot += score.apply(book.id)
              count += 1
            }

          hashmapScoreTotDiff.addOne(library.ID, score_tot)
        }
      }


      ////////////////////////////////// SORT LIBRARIES 2 (FILTER) //////////////////////////////////

      if (debugMode) println("H - SORT LIBRARIES 2 (FILTER)")

      def sorter2(a: Library, b: Library): Boolean = {
        var boolean = true

        // Dataset C
        if (file.equals("c_incunabula.txt"))
          boolean = hashmapScoreTotDiff.getOrElse(a.ID, 0.0) / scala.math.pow(a.signupdays, 1.25) > hashmapScoreTotDiff.getOrElse(b.ID, 0.0) / scala.math.pow(b.signupdays, 1.25)

        // Dataset D (SignUp is constant)
        if (file.equals("d_tough_choices.txt"))
          boolean = hashmapScoreTotDiff.getOrElse(a.ID, 0.0) > hashmapScoreTotDiff.getOrElse(b.ID, 0.0)

        // Dataset E (Different score based only on firsts (BxD * numdays))
        if (file.equals("e_so_many_books.txt"))
          boolean = hashmapScoreTotDiff.getOrElse(a.ID, 0.0) / a.signupdays > hashmapScoreTotDiff.getOrElse(b.ID, 0.0) / b.signupdays

        boolean
      }

      if (file.equals("c_incunabula.txt") || file.equals("d_tough_choices.txt") || file.equals("e_so_many_books.txt"))
        libraries = libraries.sortWith(sorter2)

      if (file.equals("d_tough_choices.txt"))
        libraries = libraries.filter(x => x.differentBooks.size > 0) // Delete library with no new books

      if (file.equals("f_libraries_of_the_world.txt"))
        libraries = libraries.filter(x => x.differentBooks.size > 15) // Delete library with few books


      ////////////////////////////////// SEND BOOKS //////////////////////////////////

      if (debugMode) println("I - SEND BOOKS")

      // Score (Writer)
      val s = new PrintWriter(new File(path + "output\\score_" + file))

      var RES_libraries: ArrayBuffer[Library] = ArrayBuffer[Library]()

      var inserted_books = ArrayBuffer[Int]() // Performance: use "Int" instead of "Book"
      inserted_books.clear() // Clear between different input

      var deadline: Int = numdays

      i = 0 // Library

      while (deadline > 0 && i < libraries.size) {

        if (deadline > libraries(i).signupdays) {

          deadline -= libraries(i).signupdays

          // Books and different books sorted before (Reverse)

          j = 0

          // Number of books to send by this library before deadline
          var k = deadline * libraries(i).booksxday.asInstanceOf[Long] // Use Long insted of Int (Upper bound)

          // Priority to different books
          while (j < libraries(i).differentBooks.size && k > 0) {

            if (!inserted_books.contains(libraries(i).differentBooks(j).id)) {
              libraries(i).booksToSend.addOne(libraries(i).differentBooks(j))
              inserted_books.addOne(libraries(i).differentBooks(j).id)
              k -= 1
            }
            j += 1
          }

          j = 0

          while (j < libraries(i).books.size && k > 0) {

            if (!inserted_books.contains(libraries(i).books(j).id)) {
              libraries(i).booksToSend.addOne(libraries(i).books(j))
              inserted_books.addOne(libraries(i).books(j).id)
              k -= 1
            }
            j += 1
          }

          s.append(libraries(i) + ", Deadline: " + deadline.toString + ", ScoreHeuristic: " + hashmapScoreHeuristic.getOrElse(libraries(i).ID, 0.0) + "\n")

          RES_libraries.addOne(libraries(i))
        }

        i += 1 // Next library
      }

      s.append("END")
      s.close()


      ////////////////////////////////// OUTPUT //////////////////////////////////

      if (debugMode) println("J - OUTPUT")

      RES_libraries = RES_libraries.filter(_.booksToSend.size > 0)

      var RES_num_libraries: Int = RES_libraries.length

      val writer = new PrintWriter(new File(path + "output\\output_" + file))

      writer.append(RES_num_libraries.toString + "\n")
      RES_libraries.foreach(library => {
        writer.append(library.ID + " " + library.booksToSend.size + "\n")
        library.booksToSend.foreach(book => writer.append(book.id + " "))
        writer.append("\n")
      })

      writer.close

      // Score estimation
      var score_estimation = 0
      RES_libraries.foreach(l =>
        l.booksToSend.foreach(b =>
          score_estimation += score.apply(b.id)
        )
      )

      println("Score estimation: " + score_estimation)

      val endTime = System.nanoTime
      val elapsedSeconds = (endTime - startTime) / 1e9d

      println("Duration: " + elapsedSeconds + "s")

      println("[FINISHED PROBLEM " + file + "]")
    })
  }
}
