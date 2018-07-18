package server

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.webapp.Configuration.ClassList
import org.eclipse.jetty.webapp.WebAppContext
import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

class Jetty (private val port: Int = 8080){

    private val server: Server by lazy {
        Server(port)
    }

    fun start(){
        val servletContext: ServletContextHandler = WebAppContext()
        servletContext.resourceBase = "web/WEB-INF"
        servletContext.contextPath = "/"
        val classList: ClassList = ClassList.setServerDefault(server)
        classList.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration",
                "org.eclipse.jetty.plus.webapp.EnvConfiguration",
                "org.eclipse.jetty.plus.webapp.PlusConfiguration")
        classList.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration")

        servletContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/[^/]*jstl.*\\.jar$")

        servletContext.addEventListener(JettyContextListener())

        val staticResourceHandler = ContextHandler()
        staticResourceHandler.contextPath = "/css"
        val resourceHandler = ResourceHandler()
        resourceHandler.resourceBase = "src/main/resources/static/css"

        servletContext.handler = resourceHandler
        val handlers = HandlerList()
        handlers.handlers = arrayOf(staticResourceHandler, servletContext)
        server.handler = handlers

        try{
            server.start()
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

}

class JettyContextListener : ServletContextListener {
    override fun contextInitialized(sce: ServletContextEvent) {
        val servletContext: ServletContext = sce.servletContext

    }

    override fun contextDestroyed(sce: ServletContextEvent) {

    }

}