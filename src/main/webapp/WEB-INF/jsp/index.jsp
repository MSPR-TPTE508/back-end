<!DOCTYPE HTML>
<html lang="fr">
    <head>
        <meta charset="UTF-8" />
        <title>Connexion</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/w3.css" />
    </head>

    <body class="w3-light-grey">
        <%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@page import="java.util.*" session="true" %>
        
        <header class="w3-container w3-display-topmiddle w3-teal" style="width: 100%">
            <h1 style="text-align: center;">Clinique</h1>
        </header>

        <main class="w3-round w3-container w3-teal w3-display-middle w3-padding w3-card">
            <form method="post" action="/login">
                <sec:authorize access="isAnonymous()">
                    <label for="username">Nom d'utilisateur</label><br>
                    <input type="text" id="username" name="username" class="w3-input w3-border w3-round"><br>
                    <label for="password">Mot de passe:</label><br>
                    <input type="password" id="password" name="password" class="w3-input w3-border w3-round"><br>
                </sec:authorize>

                <sec:authorize access="hasRole('ROLE_PRE_AUTHENTICATED')">
                    <label for="otp">Mot de passe à usage unique</label><br>
                    <input type="text" id="otp" name="otp" class="w3-input w3-border w3-round"><br>
                </sec:authorize>

                <!-- Without csrf token, you will be unable to log in -->
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                <input type="submit" class="w3-input w3-border w3-round">
            </form>
                
            <div class="w3-panel w3-pale-red w3-leftbar w3-border-red">
                <p>${SPRING_SECURITY_LAST_EXCEPTION.message}</p>
            </div>
        </main>

        <footer class="w3-container w3-teal w3-display-bottommiddle" style="text-align: center; width: 100%">
            <p>2022 EPSI - MSPR 500</p>
        </footer>
    </body>
</html>