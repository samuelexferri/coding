object Main {

  def main(args: Array[String]): Unit = {

    val files: List[String] = List[String](
      "a_solar.txt",
      "b_dream.txt",
      "c_soup.txt",
      "d_maelstrom.txt",
      "e_igloos.txt",
      "f_glitch.txt"
    )

    files.foreach(file => new Solver(file).start())
  }
}
