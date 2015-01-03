package scalatron.webServer.servelets

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}


case class RegistrationServlet(context: WebContext) extends BaseServlet with CreateUser {

    // GET /register -> display registration form
    override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
        val result = loadRelTextFile("registration.html")
        serveString(result, request, response)
    }

    // POST /register -> register new user account
    override def doPost(request: HttpServletRequest, response: HttpServletResponse) {
        handleCreateUserPage(request, response, "/", "main page")
    }

}