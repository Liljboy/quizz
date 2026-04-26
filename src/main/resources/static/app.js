const QUIZ_ID = 'q1'; // L'ID du quiz par défaut défini dans data.sql
let quizData = null;
let currentQuestionIndex = 0;
let selectedAnswers = {};

const views = {
    start: document.getElementById('start-screen'),
    quiz: document.getElementById('quiz-screen'),
    result: document.getElementById('result-screen')
};

const questionContainer = document.getElementById('question-container');
const progressBar = document.getElementById('progress-bar');
const startBtn = document.getElementById('start-btn');

startBtn.addEventListener('click', initQuiz);

async function loadQuizData() {
    try {
        const response = await fetch(`/api/quiz/${QUIZ_ID}`);
        if (!response.ok) throw new Error('Erreur de chargement du quiz');
        quizData = await response.json();
    } catch (e) {
        console.error(e);
        alert('Impossible de charger le quiz. Assurez-vous que le backend est lancé.');
    }
}

async function initQuiz() {
    if (!quizData) {
        startBtn.textContent = 'Chargement...';
        await loadQuizData();
    }
    
    if (quizData) {
        switchView('quiz');
        renderQuestion();
    }
}

function switchView(viewName) {
    Object.values(views).forEach(v => v.classList.remove('active'));
    views[viewName].classList.add('active');
}

function updateProgress() {
    const total = quizData.questions.length;
    const progress = ((currentQuestionIndex) / total) * 100;
    progressBar.style.width = `${progress}%`;
}

function renderQuestion() {
    updateProgress();
    
    // Formater la question
    const question = quizData.questions[currentQuestionIndex];
    const letters = ['A', 'B', 'C', 'D', 'E', 'F'];
    
    // Préparer le DOM
    let html = `
        <div id="q-content" class="fade-in-up">
            <h2 class="question-text">${currentQuestionIndex + 1}. ${question.texte}</h2>
            <div class="options-container">
                ${question.options.map((opt, index) => `
                    <button class="option-btn" data-opt-id="${opt.id}" data-q-id="${question.id}">
                        <span class="badge">${letters[index]}</span>
                        <span class="opt-text">${opt.texte}</span>
                    </button>
                `).join('')}
            </div>
        </div>
    `;
    
    questionContainer.innerHTML = html;
    
    // Attacher les écouteurs sur les options
    const optionBtns = document.querySelectorAll('.option-btn');
    optionBtns.forEach(btn => {
        btn.addEventListener('click', handleOptionClick);
    });
}

function handleOptionClick(e) {
    const btn = e.currentTarget;
    const qId = btn.getAttribute('data-q-id');
    const optId = btn.getAttribute('data-opt-id');
    
    // Surligner la sélection
    document.querySelectorAll('.option-btn').forEach(b => b.classList.remove('selected'));
    btn.classList.add('selected');
    
    // Sauvegarder la réponse
    selectedAnswers[qId] = optId;
    
    // Animer la sortie
    setTimeout(() => {
        const qContent = document.getElementById('q-content');
        qContent.classList.remove('fade-in-up');
        qContent.classList.add('fade-out-up');
        
        setTimeout(() => {
            currentQuestionIndex++;
            if (currentQuestionIndex < quizData.questions.length) {
                renderQuestion();
            } else {
                finishQuiz();
            }
        }, 300); // attend que la transition finisse
        
    }, 400); // petit délai avant de passer à la suite, comme typeform
}

async function finishQuiz() {
    updateProgress(); // barre à 100%
    
    try {
        const response = await fetch(`/api/quiz/${QUIZ_ID}/soumettre`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ reponsesSelectionnees: selectedAnswers })
        });
        
        const resultat = await response.json();
        
        document.getElementById('score-text').textContent = `${Math.round(resultat.pourcentage)}%`;
        document.getElementById('score-message').textContent = resultat.message;
        
        switchView('result');
    } catch (e) {
        console.error(e);
        alert('Erreur lors de la soumission du quiz.');
    }
}
