package server

fun main(args: Array<String>) {

    val jetty: Jetty = Jetty(8080)
    jetty.start()

}