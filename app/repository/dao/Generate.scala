package repository.dao

object Generate {
  def apply: Unit = {
    val slickDriver = "slick.jdbc.PostgresProfile"
    val jdbcDriver = "org.postgresql.Driver"
    val url = "jdbc:postgresql://localhost:5432/pomodoro"
    val outputFolder = "app"
    val `package` = "repository.dao"
    val user = "postgres"
    val password = "pomodoro"

    slick.codegen.SourceCodeGenerator.main(
      Array(slickDriver,
            jdbcDriver,
            url,
            outputFolder,
            `package`,
            user,
            password)
    )
  }
}
