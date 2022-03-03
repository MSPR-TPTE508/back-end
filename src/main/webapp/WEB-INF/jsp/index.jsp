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
    
        <header class="w3-container w3-display-topmiddle">
            <h1>Welcome </h1>
        </header>

        <main class="w3-container w3-display-middle">
            <form method="post" action="/login">
                <sec:authorize access="isAnonymous()">
                    <label for="username">Username</label><br>
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

        <footer class="w3-container w3-display-bottommiddle">
            <p>2022 EPSI - MSPR 500</p>
        </footer>
</body>

</html>