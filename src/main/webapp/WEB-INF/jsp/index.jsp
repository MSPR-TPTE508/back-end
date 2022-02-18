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
            <form method="POST">
                <label for="femail">Adresse e-mail:</label><br>
                <input type="text" id="femail" name="femail" class="w3-input w3-border w3-round"><br>
                <label for="fpassword">Mot de passe:</label><br>
                <input type="text" id="fpassword" name="fpassword" class="w3-input w3-border w3-round"><br>
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