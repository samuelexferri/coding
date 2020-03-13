class ProjectManager(var company: String, var bonus: Int, var position: Int, var x: Int, var y: Int) {
  override def toString: _root_.java.lang.String = position + ": " + company + " " + bonus
}
