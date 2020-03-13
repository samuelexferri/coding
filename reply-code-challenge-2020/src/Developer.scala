class Developer(var company: String, var bonus: Int, var skills: List[String], var position: Int, var x: Int, var y: Int, var score: Int) {
  override def toString: _root_.java.lang.String = company + " " + bonus + " " + skills
}
