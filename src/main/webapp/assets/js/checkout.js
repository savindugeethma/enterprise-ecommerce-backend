
window.addEventListener("load" , async () =>{
    try{
        Notiflix.Loading.pulse("Data is loading", {
            clickToClose: false,
            svgColor: '#0284c7'
        });

        await loadAddress();
        await loadCheckOutData();
        await loadCheckoutOrderSummary();

    }finally {
        Notiflix.Loading.remove();

    }
})
document.addEventListener("DOMContentLoaded", () => {
    loadAddress();
    loadCheckoutOrderSummary();

});

async function loadAddress() {

    try {
        const response = await fetch("api/profiles/addresses");
        if (response.ok) {
            const data = await response.json();
            console.log(data);
            renderOrderAddresses(data);
            console.log("response.ok");
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    } finally {
        Notiflix.Loading.remove();
    }
}

function renderOrderAddresses(userData) {
    if (!userData) {
        console.error("Error: User data object එකක් ලැබුණේ නැහැ මචන්!");
        return;
    }

    console.log("Rendering billing inputs only...", userData);

    // 1. Name එක First Name සහ Last Name බොක්ස් වලට දානවා
    if (userData.name) {
        const nameParts = userData.name.trim().split(" ");
        const firstName = nameParts[0] || "";
        const lastName = nameParts.slice(1).join(" ") || "";

        const firstNameInput = document.getElementById("first-name");
        const lastNameInput = document.getElementById("last-name");

        if (firstNameInput) firstNameInput.value = firstName;
        if (lastNameInput) lastNameInput.value = lastName;
    }

    // 2. Email එක සෙට් කරනවා
    const emailInput = document.getElementById("email");
    if (emailInput && userData.email) {
        emailInput.value = userData.email;
    }

    // 3. Addresses Array එකෙන් ඩේටා ටික විතරක් Render කරනවා
    if (userData.addresses && userData.addresses.length > 0) {

        // 🔍 [SAFE FIX]: isPrimary එක true හෝ "true" ද කියලා ලිහිල්ව චෙක් කරනවා (==).
        // මුකුත් නැත්නම් Array එකේ 0 වෙනි පේළියේ තියෙන එක ඔටෝම ගන්නවා.
        const primaryAddress = userData.addresses.find(addr => addr.isPrimary == true || addr.isPrimary == "true") || userData.addresses[0];

        console.log("Selected Primary Address to render:", primaryAddress);

        if (primaryAddress) {
            const addr1Input = document.getElementById("address1");
            const addr2Input = document.getElementById("address2");
            const cityInput = document.getElementById("city");
            const postalInput = document.getElementById("postal-code");
            const phoneInput = document.getElementById("phone");
            const districtSelect = document.getElementById("shipping-district");

            // HTML Inputs වලට values ටික නූලටම ඔබනවා
            if (addr1Input) addr1Input.value = primaryAddress.lineOne || "";
            if (addr2Input) addr2Input.value = primaryAddress.lineTwo || "";
            if (postalInput) postalInput.value = primaryAddress.postalCode || "";
            if (phoneInput) phoneInput.value = primaryAddress.mobile || "";

            // City එකට lineTwo එකේ අගයම සෙට් කරනවා
            if (cityInput && primaryAddress.lineTwo) {
                cityInput.value = primaryAddress.lineTwo;
            }

            // District 25 dropdown එකට lineTwo එකේ අගය Capital කරලා මැච් කරනවා
            if (districtSelect && primaryAddress.lineTwo) {
                const rawDistrict = primaryAddress.lineTwo.trim();
                const formattedDistrict = rawDistrict.charAt(0).toUpperCase() + rawDistrict.slice(1).toLowerCase();

                districtSelect.value = formattedDistrict;
                console.log("Setting district dropdown to:", formattedDistrict);

                // Select2 plugin එක පාවිච්චි කරනවා නම් UI එක update වෙන්න මේක ඕනේ බන්
                if (typeof $ !== 'undefined' && $(districtSelect).hasClass('select2')) {
                    $(districtSelect).trigger('change');
                }
            }
        } else {
            console.error("Error: Array එක ඇතුළේ address object එකක් හොයාගන්න බැහැ බන්!");
        }
    } else {
        console.error("Error: userData එක ඇතුළේ addresses array එකක් නැහැ හෝ හිස්!");
    }
}

async function loadCheckOutData() {
    try{
        const response = await fetch ("api/checkouts/user-checkout-data")
        if (response.ok){
            const data = await response.json();
            if (data.status){

            }else {
                Notiflix.Notify.failure(data.message, {
                    position: 'center-top'
                });
            }
        }else {
            Notiflix.Notify.failure("City data loading fail", {
                position: 'center-top'
            });
        }
    }catch{
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

async function loadCheckoutOrderSummary() {
    try {
        console.log("Fetching cart items for checkout...");

        if (typeof Notiflix !== 'undefined') {
            Notiflix.Loading.pulse("Loading Order Details...", {
                clickToClose: false,
                svgColor: '#0284c7'
            });
        }

        const response = await fetch('api/carts/all-carts');

        if (response.ok) {
            const data = await response.json();
            console.log("Checkout Cart Data Received:", data);

            if (data.status && data.cartItems) {
                renderCheckoutSummary(data.cartItems);
            } else {
                console.error("Backend Error: " + data.message);
                window.location.replace("empty-cart.html");
            }
        } else {
            console.error("HTTP Error Status: " + response.status);
            if (typeof Notiflix !== 'undefined') {
                Notiflix.Notify.failure("Failed to load checkout details!");
            }
        }
    } catch (e) {
        console.error("Error fetching checkout data:", e);
        if (typeof Notiflix !== 'undefined') {
            Notiflix.Notify.failure(e.message);
        }
    } finally {
        if (typeof typeof Notiflix !== 'undefined') {
            Notiflix.Loading.remove();
        }
    }
}

function renderCheckoutSummary(cartItems) {
    const tableBody = document.getElementById("checkout-products-body");
    if (!tableBody) {
        console.error("Error: #checkout-products-body container!");
        return;
    }

    tableBody.innerHTML = "";
    let orderSubtotal = 0;
    let hasHeavyItem = false;

    if (cartItems && cartItems.length > 0) {
        cartItems.forEach(item => {

            const productTitle = item.productTitle || "Product Item";
            const productPrice = item.price || 0;
            const itemQty = item.qty || 0;

            const itemTotal = itemQty * productPrice;
            orderSubtotal += itemTotal;

            if (item.isHeavy === true || item.heavy === true) {
                hasHeavyItem = true;
            }

            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td class="Product-name">
                    <p>${productTitle} <strong>× ${itemQty}</strong></p>
                </td>
                <td class="Product-price">
                    <p>Rs. ${Number(itemTotal).toFixed(2)}</p>
                </td>
            `;

            tableBody.appendChild(tr);
        });
    }

    let shippingFee = 0;
    if (hasHeavyItem) {
        shippingFee = 5000;
    } else if (orderSubtotal > 0) {
        shippingFee = 500;
    }

    let grandTotal = orderSubtotal + shippingFee;

    const subtotalElement = document.getElementById("checkout-subtotal");
    const grandTotalElement = document.getElementById("checkout-grand-total");

    if (subtotalElement) subtotalElement.innerText = `Rs. ${Number(orderSubtotal).toFixed(2)}`;
    if (grandTotalElement) grandTotalElement.innerText = `Rs. ${Number(grandTotal).toFixed(2)}`;


    const flatRateLabel = document.querySelector('label[for="radio1"]');
    if (flatRateLabel) {
        const deliveryMethod = hasHeavyItem ? "Truck Delivery" : "Standard Shipping";
        flatRateLabel.innerHTML = `<span></span> Flat Rate (${deliveryMethod}: Rs. ${Number(shippingFee).toFixed(2)})`;
    }

    //session ekakata danawa - slip ekata ganna
    const checkoutPricing = {
        subtotal: orderSubtotal,
        shipping: shippingFee,
        grandTotal: grandTotal,
        cartItems: cartItems
    };
    sessionStorage.setItem("checkout_computed_prices", JSON.stringify(checkoutPricing));

    console.log(`Checkout Table Rendered! Heavy Items Present: ${hasHeavyItem} | Shipping: Rs. ${shippingFee} ✅🔥`);
}

async function handlePlaceOrder() {
    console.log("Place Order trigger වුණා! 🚀");

    // 1. වම් පැත්තේ Billing Form එකේ values ටික උඹේ පිරිසිදුම IDs වලින්ම අල්ලගන්නවා
    const firstName = document.getElementById("first-name")?.value.trim();
    const lastName = document.getElementById("last-name")?.value.trim();
    const addrLine1 = document.getElementById("address1")?.value.trim();
    const addrLine2 = document.getElementById("address2")?.value.trim();
    const city = document.getElementById("city")?.value.trim();
    const district = document.getElementById("shipping-district")?.value; // දිස්ත්‍රික්ක 25ම තියෙන select එක
    const postalCode = document.getElementById("postal-code")?.value.trim();
    const phone = document.getElementById("phone")?.value.trim();
    const email = document.getElementById("email")?.value.trim();

    // 🧼 Required fields ටික හිස්ද කියලා Validate කරනවා
    if (!firstName || !lastName || !addrLine1 || !city || !district || !phone || !email) {
        if (typeof Notiflix !== 'undefined') {
            Notiflix.Notify.failure("Please fill in all required (*) fields! ❌", { position: 'center-top' });
        } else {
            alert("Please fill in all required (*) fields!");
        }
        return;
    }

    // 🏠 ඇඩ්‍රස් කෑලි දෙක එකතු කරලා පිරිසිදු address string එකක් හදනවා
    const fullAddress = addrLine2 ? `${addrLine1}, ${addrLine2}` : addrLine1;

    // 🔍 2. යූසර් තෝරලා තියෙන Payment Method එක (COD ද CARD ද) කියලා බලනවා
    const selectedPaymentMethod = document.querySelector('input[name="payment_method"]:checked')?.value;
    console.log("Selected Payment Method:", selectedPaymentMethod);

    if (!selectedPaymentMethod) {
        if (typeof Notiflix !== 'undefined') Notiflix.Notify.failure("Please select a payment method!");
        return;
    }

    const generatedOrderId = "ORD-" + Date.now();

    const orderData = {
        orderId: generatedOrderId,
        firstName: firstName,
        lastName: lastName,
        address: fullAddress,
        city: city,
        district: district,
        postalCode: postalCode,
        phone: phone,
        email: email,
        paymentMethod: selectedPaymentMethod
    };

    // 🎛️ ==================== PAYMENT ROUTING ====================
    if (selectedPaymentMethod === "COD") {

        // ✨ Cash on Delivery නම් කෙලින්ම සේဝ် කරන්න Backend යවනවා
        await submitCODOrder(orderData);

    } else if (selectedPaymentMethod === "CARD") {

        // 💳 Card Payments නම් PayHere Sandbox එකට ඩේටා Generate කරන්න යවනවා
        await initiatePayHerePayment(orderData);

    }
}


let checkoutCartItems = [];

async function submitCODOrder(orderData) {
    try {
        if (typeof Notiflix !== 'undefined') Notiflix.Loading.pulse("Placing your order...");

        const response = await fetch("api/checkout/place-cod", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(orderData)
        });

        // if (response.ok) {
        //     const data = await response.json();
        //     if (data.status) {
        //         if (typeof Notiflix !== 'undefined') Notiflix.Notify.success("Order Placed Successfully! 🎉");
        //         window.location.replace(`order-success.html?orderId=${data.orderId}`);
        //     } else {
        //         alert("Order Failed: " + data.message);
        //     }
        // } else {
        //     console.error("Server Error Status:", response.status);
        // }

        if (response.ok) {
            const data = await response.json();

            if (data.status) {
                if (typeof Notiflix !== 'undefined') Notiflix.Notify.success("Order Placed Successfully! 🎉");


                const computedPricesStr = sessionStorage.getItem("checkout_computed_prices");
                const pricingData = computedPricesStr ? JSON.parse(computedPricesStr) : { subtotal: 0, shipping: 0, grandTotal: 0, cartItems: [] };


                const firstName = document.getElementById("first-name") ? document.getElementById("first-name").value.trim() : "";
                const lastName = document.getElementById("last-name") ? document.getElementById("last-name").value.trim() : "";
                const emailVal = document.getElementById("email") ? document.getElementById("email").value.trim() : "";
                const phoneVal = document.getElementById("phone") ? document.getElementById("phone").value.trim() : "";

                const addr1 = document.getElementById("address-1") ? document.getElementById("address-1").value.trim() : "";
                const addr2 = document.getElementById("address-2") ? document.getElementById("address-2").value.trim() : "";
                const cityVal = document.getElementById("city") ? document.getElementById("city").value.trim() : "";
                const districtVal = document.getElementById("district") ? document.getElementById("district").value.trim() : "";
                const postcodeVal = document.getElementById("postcode") ? document.getElementById("postcode").value.trim() : "";

                let addressParts = [];
                if (addr1) addressParts.push(addr1);
                if (addr2) addressParts.push(addr2);
                if (cityVal) addressParts.push(cityVal);
                if (districtVal) addressParts.push(districtVal);
                if (postcodeVal) addressParts.push(`(${postcodeVal})`);
                let fullAddress = addressParts.join(", ");


                const successOrderDetails = {
                    orderId: data.orderId,
                    cartItems: pricingData.cartItems,
                    prices: {
                        subtotal: pricingData.subtotal,
                        shipping: pricingData.shipping,
                        grandTotal: pricingData.grandTotal
                    },
                    billing: {
                        name: `${firstName} ${lastName}`.trim() || "Savindu Geethma",
                        email: emailVal || "geethma@gmail.com",
                        mobile: phoneVal || "0786593962",
                        address: fullAddress || "Ratnapura"
                    }
                };


                const sessOrder = sessionStorage.setItem("placed_order_info", JSON.stringify(successOrderDetails));


                sessionStorage.removeItem("checkout_computed_prices");

                window.location.replace(`order-success.html?orderId=${data.orderId}`);
            } else {
                alert("Order Failed: " + data.message);
            }
        }

    } catch (e) {
        console.error("COD Submission Error:", e);
    } finally {
        if (typeof Notiflix !== 'undefined') Notiflix.Loading.remove();
    }
}

async function initiatePayHerePayment(orderData) {
    try {
        if (typeof Notiflix !== 'undefined') {
            Notiflix.Loading.pulse("Preparing secure payment...");
        }

        // 📡 1. Backend Java එකට ඩේටා යවලා සර්ගේ ක්‍රමයට හදපු Hash එකයි Params ටිකයි ඉල්ලනවා
        const response = await fetch("api/checkout/initiate-card-payment", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(orderData)
        });

        if (response.ok) {
            const payHereParams = await response.json();

            if (payHereParams.status) {
                console.log("Java Backend එකෙන් ආපු පිරිසිදුම Parameters:", payHereParams);

                // 🎯 2. PAYHERE JAVASCRIPT SDK OBJECT එක සතයටම සකස් කරගන්නවා
                const payment = {
                    "sandbox": true,                           // 🛠️ Sandbox ටෙස්ට් නිසා true කරනවා
                    "merchant_id": payHereParams.merchantId,   // Util එකෙන් එන Merchant ID එක
                    "return_url": payHereParams.returnUrl,
                    "cancel_url": payHereParams.cancelUrl,
                    "notify_url": payHereParams.notifyUrl,
                    "order_id": payHereParams.orderId,         // Java එකෙන් එවපු සිරාම Order ID එක
                    "items": payHereParams.itemsName,
                    "amount": payHereParams.amount,             // Java එකෙන් එවපු "1052.00" String එකමයි
                    "currency": "LKR",
                    "first_name": payHereParams.firstName,
                    "last_name": payHereParams.lastName,
                    "email": payHereParams.email,
                    "phone": payHereParams.phone,
                    "address": payHereParams.address,
                    "city": payHereParams.city,
                    "country": "Sri Lanka",
                    "hash": payHereParams.hash                 // 🔐 සර්ගේ Util එකෙන් හැදුණු 200% නිවැරදි Hash එක
                };

                console.log("Submitting this exact object to PayHere SDK:", payment);

                // 🚀 3. PAYHERE MODAL POPUP එක ක්ෂණිකව ස්ටාර්ට් කරනවා බන්!
                payhere.startPayment(payment);

            } else {
                alert("Payment initiation failed: " + payHereParams.message);
            }
        } else {
            console.error("Server Error Status:", response.status);
        }
    } catch (e) {
        console.error("PayHere Init Error:", e);
    } finally {
        if (typeof Notiflix !== 'undefined') {
            Notiflix.Loading.remove();
        }
    }
}


if (typeof payhere !== 'undefined') {

    // 💵 පේමන්ට් එක සාර්ථකව ඉවර වුණාම බ්‍රවුසර් එකේදී මේක දුවනවා
    payhere.onCompleted = function onCompleted(orderId) {
        console.log("Payment completed for orderId : " + orderId);
        // ➡️ කෙලින්ම අපේ සාර්ථක වූ පේජ් එකට රීඩිරෙක්ට් කරනවා
        window.location.replace(`order-success.html?orderId=${orderId}`);
    };

    // ❌ පේමන්ට් එක මැදින් ක්ලෝස් කරලා දැම්මොත් මේක දුවනවා
    payhere.onDismissed = function onDismissed() {
        console.log("Payment dismissed by user.");
        if (typeof Notiflix !== 'undefined') {
            Notiflix.Notify.warning("Payment canceled or dismissed! 🛒");
        }
    };

    // 🚨 මොකක් හරි Gateway ලෙඩක් ආවොත් මේක දුවනවා
    payhere.onError = function onError(error) {
        console.log("PayHere Error : " + error);
        alert("PayHere Gateway Error: " + error);
    };
}