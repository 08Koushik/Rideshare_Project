// BASE URL for backend API
const BASE_URL = "http://localhost:8080/api/auth";

// =================== USER LOGIN ===================
const userLoginForm = document.getElementById("userLoginForm");
if (userLoginForm) {
    userLoginForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        // --- CLEAN RETRIEVAL ---
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        // Check for empty values immediately
        if (!email || !password) {
            //alert("Please enter both email and password.");
            Swal.fire({
                            icon: 'warning',
                            title: 'Required Fields',
                            text: "Please enter both email and password."
                        });
            return;
        }

        // Encode parameters for the URL
        const fetchUrl = `${BASE_URL}/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`;
        console.log("Attempting to fetch URL:", fetchUrl);

       // In src/main/resources/static/js/user.js

       // ... inside the addEventListener function ...
               try {
                   // ... (keep your email and password retrieval code)

                   const response = await fetch(fetchUrl, { method: "POST" });

                   // Read the full response body as text first to check for errors/exceptions
                   // We use a safe way to handle the text response
                   const resultText = await response.text();

                   // Check for the special 'FIRST_LOGIN' string returned by the controller
                   if (resultText.includes("FIRST_LOGIN")) {
                       localStorage.setItem("userEmail", email);
                       window.location.href = "reset-password.html";
                   }
                   // If it's not FIRST_LOGIN or an error string, assume it's the User JSON object
                   else if (resultText.trim().startsWith("{")) {
                       const user = JSON.parse(resultText);

                       // *** CRITICAL FIX: SAVE THE FULL USER OBJECT ***
                       localStorage.setItem("loggedInUser", JSON.stringify(user));
                       window.location.href = "user-home.html";
                   } else {
                       // If it's another error message (e.g., "User not found!")
                       //alert("Invalid credentials. Please try again. Backend response: " + resultText);
                       Swal.fire({
                                                   icon: 'error',
                                                   title: 'Login Failed',
                                                   text: "Invalid credentials. Please try again. Backend response: " + resultText
                                              });
                   }

               } catch (err) {
                   console.error("Fetch error:", err);
                   //alert("Error connecting to server. Check console for details.");
                   Swal.fire({
                                           icon: 'error',
                                           title: 'Connection Error',
                                           text: "Error connecting to server. Check console for details."
                                      });
               }
       // ...
    });
}