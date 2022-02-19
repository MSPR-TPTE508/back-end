<!DOCTYPE HTML>
<html>
   <head>
      <meta charset="UTF-8" />
      <title>Welcome</title>
      <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
      <!--<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/style.css"/>-->
   </head>
   <body>
        <header class="w3-container w3-display-topmiddle">
            <h1>Welcome</h1>
        </header>

        <main class="w3-container w3-display-middle">
            <form method="post" action="/login">
                <label for="username">Adresse e-mail:</label><br>
                <input type="text" id="username" name="username" class="w3-input w3-border w3-round"><br>
                <label for="password">Mot de passe:</label><br>
                <input type="password" id="password" name="password" class="w3-input w3-border w3-round"><br>

                <!-- Without csrf token, you will be unable to log in -->
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                
                <input type="submit" class="w3-input w3-border w3-round">
            <form>

			<div class="w3-panel w3-pale-red w3-leftbar w3-border-red">
            	<p>${message}</p>
			</div>
        </main>

        <footer class="w3-container w3-display-bottommiddle">
            <p>2022 EPSI - MSPR 500</p>
        </footer>
    </body>
</html>