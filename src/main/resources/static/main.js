document.addEventListener("DOMContentLoaded", () => {
    // Theme toggle
    const themeBtn = document.getElementById("toggle-theme");
    themeBtn.addEventListener("click", () => {
        const html = document.documentElement;
        const current = html.getAttribute("data-theme");
        html.setAttribute("data-theme", current === "dark" ? "light" : "dark");
        themeBtn.textContent = current === "dark" ? "🌙" : "☀️";
    });

    // Shorten form
    const form = document.getElementById("shorten-form");
    const longUrlInput = document.getElementById("long-url");
    const customAliasInput = document.getElementById("custom-alias");
    const resultDiv = document.getElementById("result");
    const shortUrlSpan = document.getElementById("short-url");
    const copyBtn = document.getElementById("copy-btn");
    const errorDiv = document.getElementById("error");
    const recentList = document.getElementById("recent-list");

    // Load recent URLs from localStorage
    function loadRecent() {
        recentList.innerHTML = "";
        const recents = JSON.parse(localStorage.getItem("recentUrls") || "[]");
        recents.slice(0, 5).forEach(item => {
            const li = document.createElement("li");
            li.innerHTML = `<a href="${item.shortUrl}" target="_blank">${item.shortUrl}</a> <span title="${item.originalUrl}">🔗</span>`;
            recentList.appendChild(li);
        });
    }
    loadRecent();

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        errorDiv.textContent = "";
        resultDiv.classList.add("hidden");
        const url = longUrlInput.value.trim();
        const customAlias = customAliasInput.value.trim() || undefined;

        if (!url) {
            errorDiv.textContent = "Please enter a URL.";
            return;
        }

        try {
            const res = await fetch("/api/shorten", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ url, customAlias })
            });
            const data = await res.json();
            if (!res.ok) {
                errorDiv.textContent = data.error || "Failed to shorten URL.";
                return;
            }
            shortUrlSpan.textContent = data.shortUrl;
            shortUrlSpan.setAttribute("data-url", data.shortUrl);
            resultDiv.classList.remove("hidden");

            // Save to recent
            const recents = JSON.parse(localStorage.getItem("recentUrls") || "[]");
            recents.unshift({ shortUrl: data.shortUrl, originalUrl: url });
            localStorage.setItem("recentUrls", JSON.stringify(recents.slice(0, 10)));
            loadRecent();
        } catch (err) {
            errorDiv.textContent = "Network error. Please try again.";
        }
    });

    copyBtn.addEventListener("click", () => {
        const url = shortUrlSpan.getAttribute("data-url");
        if (url) {
            navigator.clipboard.writeText(url);
            copyBtn.textContent = "Copied!";
            setTimeout(() => (copyBtn.textContent = "Copy"), 1200);
        }
    });
});