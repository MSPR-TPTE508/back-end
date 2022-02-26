<!DOCTYPE HTML>
<html>


<head>
    <meta charset="UTF-8" />
    <title>Welcome</title>
    <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
    <script src="https://www.google.com/recaptcha/enterprise.js?render=6Le_Op8eAAAAAInmsW4WSOIFaGQqkXHXIzn--YvO"></script>

    <!--<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/style.css"/>-->
</head>

<body>
    <%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@page import="java.util.*" session="true" %>

        <main class="w3-container w3-display-middle">
            <div class="w3-panel w3-green ">
                <h3 class="w3-margin">Success !</h3>
                <p class="w3-margin">${message}</p>
                <div class="w3-container w3-margin w3-right-align">
                    <a href="/login" class="w3-button w3-black">Se connecter</a>
                </div>
              </div>
        </main>

        <footer class="w3-container w3-display-bottommiddle">
            <p>2022 EPSI - MSPR 500</p>
        </footer>
</body>

</html>