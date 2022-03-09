<!DOCTYPE HTML>
<html lang="fr">
    <head>
        <meta charset="UTF-8" />
        <title>Connecté</title>
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

        <main class="w3-container w3-display-middle">
            <div class="w3-panel w3-green ">
                <h3 class="w3-margin">Succes !</h3>
                <p class="w3-margin">${message}</p>
                <div class="w3-container w3-margin w3-right-align">
                    <a href="/login" class="w3-button w3-black">Se connecter</a>
                </div>
            </div>
        </main>

        <footer class="w3-container w3-teal w3-display-bottommiddle" style="text-align: center; width: 100%">
            <p>2022 EPSI - MSPR 500</p>
        </footer>
    </body>
</html>