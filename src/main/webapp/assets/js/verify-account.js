
console.log("🚀 SmartTrade Verification JS Connected...");

let params = new URLSearchParams(window.location.search);
const userEmail = params.get("email");
const fullCode = params.get("verificationCode");

document.addEventListener("DOMContentLoaded", function() {
    const inputs = document.querySelectorAll(".otp-input");
    const btnVerify = document.getElementById("btnVerify");
    const hiddenInput = document.getElementById("verificationCode");
    const timerContainer = document.getElementById("timerContainer");
    const resendContainer = document.getElementById("resendContainer");
    const timerSpan = document.getElementById("timer");
    const btnResend = document.getElementById("btnResend");
    const verifyForm = document.getElementById("verifyForm");

    if (inputs.length > 0) {
        inputs[0].focus();
    }

    if (fullCode) {
        if (hiddenInput) hiddenInput.value = fullCode;

        const codeDigits = fullCode.split("");
        inputs.forEach((input, index) => {
            if (codeDigits[index]) {
                input.value = codeDigits[index];
            }
        });

        if (btnVerify) {
            btnVerify.removeAttribute("disabled");
            btnVerify.classList.add("is-active");
        }

        verifyAccount();
    }

    inputs.forEach((input, index) => {
        input.addEventListener("keypress", function(e) {
            if (e.key < "0" || e.key > "9") e.preventDefault();
        });

        input.addEventListener("input", function() {
            input.value = input.value.replace(/[^0-9]/g, "");
            if (input.value.length === 1 && index < inputs.length - 1) {
                inputs[index + 1].focus();
            }
            checkCodeCompleteness();
        });

        input.addEventListener("keydown", function(e) {
            if (e.key === "Backspace") {
                if (input.value.length === 0 && index > 0) {
                    inputs[index - 1].focus();
                    inputs[index - 1].value = "";
                } else {
                    input.value = "";
                }
                checkCodeCompleteness();
            }
        });
    });

    function checkCodeCompleteness() {
        let code = "";
        inputs.forEach(input => code += input.value);
        if (hiddenInput) hiddenInput.value = code;

        if (code.length === 6) {
            btnVerify.removeAttribute("disabled");
            btnVerify.classList.add("is-active");
        } else {
            btnVerify.setAttribute("disabled", "true");
            btnVerify.classList.remove("is-active");
        }
    }

    let timeRemaining = 60;
    let timerInterval;

    function startTimer() {
        timerContainer.classList.remove("d-none");
        resendContainer.classList.add("d-none");
        timeRemaining = 60;
        timerSpan.textContent = "01:00";

        clearInterval(timerInterval);
        timerInterval = setInterval(() => {
            timeRemaining--;
            let minutes = Math.floor(timeRemaining / 60);
            let seconds = timeRemaining % 60;
            minutes = minutes < 10 ? "0" + minutes : minutes;
            seconds = seconds < 10 ? "0" + seconds : seconds;
            timerSpan.textContent = minutes + ":" + seconds;

            if (timeRemaining <= 0) {
                clearInterval(timerInterval);
                timerContainer.classList.add("d-none");
                resendContainer.classList.remove("d-none");
            }
        }, 1000);
    }

    startTimer();

    if (btnResend) {
        btnResend.addEventListener("click", function(e) {
            e.preventDefault();
            alert("A new verification code has been sent to your email!");
            inputs.forEach(input => input.value = "");
            checkCodeCompleteness();
            if (inputs.length > 0) inputs[0].focus();
            startTimer();
        });
    }


    if (verifyForm) {
        verifyForm.addEventListener("submit", function(e) {
            e.preventDefault();
            verifyAccount();
        });
    }
});


async function verifyAccount() {
    if (typeof Notiflix !== 'undefined') {
        Notiflix.Loading.pulse("Verifying...", { clickToClose: false, svgColor: '#0284c7' });
    }

    let params = new URLSearchParams(window.location.search);
    const hiddenInput = document.getElementById("verificationCode");

    const verifyObj = {
        email: params.get("email"),
        code: params.get("verificationCode") || (hiddenInput ? hiddenInput.value : "")
    };

    try {
        const response = await fetch("api/users/verify", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(verifyObj)
        });

        if (response.ok) {
            const data = await response.json();
            if (data.status) {
                if (typeof Notiflix !== 'undefined') {
                    Notiflix.Report.success('SmartTrade', data.message, 'Okay', () => {
                        window.location = "login.html";
                    });
                } else {
                    alert(data.message);
                    window.location = "login.html";
                }
            } else {
                if (typeof Notiflix !== 'undefined') Notiflix.Notify.failure(data.message);
                else alert(" " + data.message);
            }
        } else {
            if (typeof Notiflix !== 'undefined') Notiflix.Notify.failure("Verification failed!");
            else alert(" Verification failed!");
        }
    } catch (e) {
        console.error(e);
    } finally {
        if (typeof Notiflix !== 'undefined') Notiflix.Loading.remove(500);
    }
}