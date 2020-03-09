// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
    ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

async function getComments(){
  const response = await fetch('/data');
  const comments = await response.json();
  const commentsListElement = document.getElementById('comments-list');
  comments.forEach((comment, index) => {
    commentsListElement.appendChild(
      createCommentElement(comment, index)
    );
  });
}

function createCommentElement(commentObject, index) {
  const commentElement = document.createElement('div');
  commentElement.innerText = commentObject.comment;
  commentElement.classList.add('comment');

  const commenterElement = document.createElement('div');
  commenterElement.innerText = commentObject.commenter;
  commenterElement.classList.add('commenter');

  const container = document.createElement('div');
  container.appendChild(commenterElement);
  container.appendChild(commentElement);

  if(index % 2 == 1){
    container.classList.add('drag-right');
  }
  return container;
}

async function getLoginStatus() {
  const response = await fetch('/login-status');
  const authInfo = await response.json();
  const commentForm = document.getElementById('comment-form');
  const loginDiv = document.getElementById('login-div');
  if(authInfo.isUserLoggedIn){
    loginDiv.style.display = 'none';
    commentForm.style.display = 'flex';

    // populate name input with previously entered name from Datastore
    const nameInput = document.getElementById('name-input');
    nameInput.value = authInfo.userName;
    
    // unhide and set logout link if logged in
    const logoutLink = document.getElementById('logout-link');
    logoutLink.style.display = 'flex';
    logoutLink.href = authInfo.logoutUrl;
  } else {
    const loginLink = document.getElementById('login-link');
    loginLink.href = authInfo.loginUrl;
  }
}
