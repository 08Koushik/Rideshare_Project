// js/booking.js
console.log("booking.js loaded");

// This file MUST NOT initialize Stripe; Stripe is created in ride-details.html
// If you want to call finalizeBooking from elsewhere, it's available as window.finalizeBooking

async function finalizeBooking(paymentIntentId, rideId, passengerId, seatsBooked) {
    try {
        const payload = { rideId, passengerId, seatsBooked };
        const res = await fetch(`/api/booking/confirm?paymentIntentId=${encodeURIComponent(paymentIntentId)}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const text = await res.text();
        return { ok: res.ok, text };
    } catch (err) {
        console.error('finalizeBooking error', err);
        return { ok: false, text: err.message || 'Network error' };
    }
}

window.finalizeBooking = finalizeBooking;

