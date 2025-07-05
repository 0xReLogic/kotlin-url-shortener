document.addEventListener("DOMContentLoaded", () => {
    // Theme toggle
    const themeBtn = document.getElementById("toggle-theme");
    themeBtn.addEventListener("click", () => {
        const html = document.documentElement;
        const current = html.getAttribute("data-theme");
        html.setAttribute("data-theme", current === "dark" ? "light" : "dark");
        themeBtn.textContent = current === "dark" ? "🌙" : "☀️";
    });

    const form = document.getElementById("stats-form");
    const codeInput = document.getElementById("short-code");
    const resultDiv = document.getElementById("stats-result");
    const errorDiv = document.getElementById("stats-error");
    const statOriginal = document.getElementById("stat-original");
    const statClicks = document.getElementById("stat-clicks");
    const statCreated = document.getElementById("stat-created");
    const statStatus = document.getElementById("stat-status");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        errorDiv.textContent = "";
        resultDiv.classList.add("hidden");
        const code = codeInput.value.trim();
        if (!code) {
            errorDiv.textContent = "Please enter a short code.";
            return;
        }
        try {
            const res = await fetch(`/api/stats/${encodeURIComponent(code)}`);
            const data = await res.json();
            if (!res.ok) {
                errorDiv.textContent = data.error || "Short code not found.";
                return;
            }
            statOriginal.textContent = data.originalUrl;
            statClicks.textContent = data.clickCount;
            statCreated.textContent = new Date(data.createdAt).toLocaleString();
            statStatus.textContent = data.isActive ? "Active" : "Inactive";
            resultDiv.classList.remove("hidden");
        } catch (err) {
            errorDiv.textContent = "Network error. Please try again.";
        }
    });
});