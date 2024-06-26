const websocket_url = "wss://game.waly.dev.br";

function getQueryVariable(variable) {
  const query = window.location.search.substring(1);
  const vars = query.split('&');
  for (let i = 0; i < vars.length; i++) {
    const pair = vars[i].split('=');
    if (decodeURIComponent(pair[0]) === variable) {
      return decodeURIComponent(pair[1]);
    }
  }
}

WebSocketConnect();


function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0,
            v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

// Exemplo de como você pode usar
const uuid = generateUUID();

function WebSocketConnect() {


  if (!hasMyIdInUrl()) {
    saveMyIdToUrl(generateUUID());
  }



  const username = getQueryVariable("username");
  const rocketsContainer = document.getElementById("rockets");
  let connectionId = getQueryVariable("id");
  const socket = new WebSocket(`${websocket_url}/endpoint?username=${username}&&id=${connectionId}`);
  let myId;
  let ranking = [];

  socket.onopen = function (event) {
    console.log("WebSocket connection established");
  };

  socket.onmessage = function (event) {
    const message = event.data;
    console.log("Message from server: ", message);

    if (message.includes("Your id is:")) {
      myId = message.split(": ")[1];
    }


    if(message.includes("Starting")){
      clearRanking()
      startGame();
    }

    if (message.includes("New connection")) {
      const nick = message.split("- ")[1];
      removeRocket(nick)
      addRocket(nick);
    }

    if (message.includes("Connections Active")) {
      const nick = message.split("- ")[1];
      removeRocket(nick)
      addRocket(nick);
    }


    if (message.includes("Connection closed")) {
      const nick = message.split("- ")[1];
      removeRocket(nick);
    }


    if (message.includes("Ranking:")) {
      try {
        const rankingStr = message.split("$$")[1];
        ranking = JSON.parse(rankingStr);
      } catch (error) {
        console.error("Erro ao processar mensagem de ranking:", error);
      }
    }


  };

  socket.onclose = function (event) {
    console.log("WebSocket connection closed");
  };

  socket.onerror = function (error) {
    console.log("WebSocket error: ", error);
  };


  function hasMyIdInUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.has('id');
  }

  function saveMyIdToUrl(id) {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set('id', id);
    const newUrl = window.location.pathname + '?' + urlParams.toString();
    window.history.pushState({ path: newUrl }, '', newUrl);
  }


  function addRocket(username) {
    const rocketDiv = document.createElement("div");
    rocketDiv.classList.add("rocket");
    const rocketImg = document.createElement("img");
    rocketImg.src = "/assets/rocket-ground.gif";
    rocketImg.alt = "";
    rocketImg.id = "rocket";
    rocketDiv.appendChild(rocketImg);
    const rocketName = document.createElement("h2");
    rocketName.textContent = username
    rocketDiv.appendChild(rocketName);
    rocketsContainer.appendChild(rocketDiv);
  }


  const overlay = document.querySelector(".overlay");

  document.getElementById("start").addEventListener("click", function () {
    setTimeout(() => {
    }, 1000);
    socket.send("start");
  });

  function startGame() {
    launchRocket();
    overlay.classList.add("blur");
    setTimeout(() => {
      overlay.classList.remove("blur");
    }, 1000);
  }

  function launchRocket() {
    setTimeout(() => {
      const rockets = document.querySelectorAll(".rocket img");
      rockets.forEach((rocketImg) => {
        rocketImg.src = "/assets/rocket-launching.gif";
        animateRocket();
      });
    }, 700);
  }

  function animateRocket() {
    setTimeout(() => {
      const rockets = document.querySelectorAll(".rocket img");
      rockets.forEach((rocketImg) => {
        rocketImg.src = "/assets/rocket.gif";
        takeOffRocket();
      });
    }, 3300);
  }

  function takeOffRocket(){
    setTimeout(() => {
      const rockets = document.querySelectorAll(".rocket img");
      rockets.forEach((rocketImg) => {
        rocketImg.src = "/assets/rocket-taking-off.gif";
        stopRockets();
      });
    }, 4300);
  }


  function stopRockets() {
    setTimeout(() => {
      const rockets = document.querySelectorAll(".rocket img");
      rockets.forEach((rocketImg) => {
        rocketImg.src = "/assets/rocket-ground.gif";
        showRanking(ranking);
      });
    }, 3000);
  }

  function clearRanking() {
    const rankingList = document.querySelector('.ranking-list ul');
    rankingList.innerHTML = ''; // Limpa o conteúdo da lista de ranking
  }



  function showRanking(jsonRanking) {
    const rankingList = document.querySelector('.ranking-list ul');
    rankingList.innerHTML = '';
    const firstPlaceId = jsonRanking[0]?.id;

    const winner = document.querySelector('.overlay .mini-winner');

    if(connectionId == firstPlaceId){
      console.log("You are the winner!")
      winner.classList.remove('mini-winner-invisible');
    }else{
      winner.classList.add('mini-winner-invisible');
    }

    document.querySelector('.ranking-list').classList.remove('ranking-list-h2');
      jsonRanking.forEach((item, index) => {
      const listItem = document.createElement('li');
      listItem.textContent = `${index + 1}. ${item.username} - ${item.time}ms`;
      rankingList.appendChild(listItem);
    });
  }

  function removeAllRockets() {
    while (rocketsContainer.firstChild) {
      rocketsContainer.removeChild(rocketsContainer.firstChild);
    }
  }

  function removeRocket(nick) {
    // Obtém todos os elementos com a classe 'rocket' dentro de 'rocketsContainer'
    const rockets = rocketsContainer.getElementsByClassName('rocket');

    // Itera sobre os foguetes encontrados
    for (let i = 0; i < rockets.length; i++) {
      // Obtém o nome do foguete atual
      const rocketName = rockets[i].querySelector('h2').textContent;

      // Verifica se o nome do foguete corresponde ao 'nick' fornecido
      if (rocketName === nick) {
        // Remove o foguete atual se o nome corresponder
        rocketsContainer.removeChild(rockets[i]);
        break; // Termina a iteração após encontrar e remover o foguete
      }
    }
  }

};
