package scalatron.webServer.servelets

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import scalatron.scalatron.impl.SourceFileCollection


trait CreateUser extends BaseServlet {

    protected def handleCreateUserPage(request: HttpServletRequest, response: HttpServletResponse, redirectUri: String, redirectName: String) {
        val userName = request.getParameter("username")
        if (userName == null || userName.isEmpty) {
            serveErrorPage("user name must not be empty", redirectUri, "return to " + redirectName, request, response)
            System.err.println("error: received empty user name")
            return
        }
        if (!context.scalatron.isUserNameValid(userName)) {
            serveErrorPage("invalid user name: '" + userName + "': must not contain special characters", redirectUri, "return to administration main page", request, response)
            System.err.println("error: received invalid user name: '" + userName + "'")
            return
        }

        // verify the password field
        val password1 = request.getParameter("password1")
        val password2 = request.getParameter("password2")
        if (password1 == null || password2 == null) {
            serveErrorPage("invalid password field", redirectUri, "return to " + redirectName, request, response)
            System.err.println("error: received invalid password field")
            return
        }

        if (password1 != password2) {
            serveErrorPage("passwords fields do not match", redirectUri, "return to " + redirectName, request, response)
            System.err.println("error: passwords fields do not match")
            return
        }

        // does that user already exist?
        try {
            val userOpt = context.scalatron.user(userName)
            userOpt match {
                case Some(user) =>
                    serveErrorPage("user already exists: '" + userName + "'", redirectUri, "return to " + redirectName, request, response)
                    System.err.println("error: user already exists: '" + userName + "'")
                    return
                case None => // OK -- user does not yet exist
            }
        } catch {
            case t: Throwable =>
                serveErrorPage("unable to verify user directory for '" + userName + "'", redirectUri, "return to " + redirectName, request, response)
                System.err.println("error: failed to check whether user exists: '" + userName + "': " + t)
                return
        }

        // create the user password config file
        try {
            context.scalatron.createUser(userName, password1, SourceFileCollection.initial(userName))
        } catch {
            case t: Throwable =>
                serveErrorPage("unable to write user configuration file for '" + userName + "'", redirectUri, "return to " + redirectName, request, response)
                System.err.println("error: unable to create user account for: '" + userName + "': " + t)
                return
        }

        response.sendRedirect(redirectUri)
    }

}