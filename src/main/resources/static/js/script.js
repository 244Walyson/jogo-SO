document.querySelector("#launch").addEventListener("click", function () {
  const username = document.querySelector("#username").value;
  const encodedUsername = encodeURIComponent(username); // Codifica o username

  // Redireciona para a página rocket.html com o username como parâmetro de consulta
  window.location.href = `rocket?username=${encodedUsername}`;
});
