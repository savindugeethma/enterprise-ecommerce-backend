// document.addEventListener("DOMContentLoaded", function () {
//     console.log("📊 Success Page Loaded! Processing Receipt...");
//
//     // 📦 1. Storage එකෙන් ඩේටා කියවීම
//     const storedOrderData = sessionStorage.getItem("placed_order_info");
//     if (!storedOrderData) {
//         console.error("❌ Error: No order data found in sessionStorage!");
//         return;
//     }
//
//     const orderInfo = JSON.parse(storedOrderData);
//     console.log("📦 Received Order Package:", orderInfo);
//
//     const urlParams = new URLSearchParams(window.location.search);
//     const orderId = urlParams.get('orderId') || orderInfo.orderId || "INV-10";
//
//     // 📅 2. Meta Data පිරවීම
//     if (document.getElementById("slip-invoice-id")) document.getElementById("slip-invoice-id").innerText = `#${orderId}`;
//     if (document.getElementById("slip-order-date")) {
//         const today = new Date();
//         document.getElementById("slip-order-date").innerText = today.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
//     }
//     if (document.getElementById("slip-payment-method")) document.getElementById("slip-payment-method").innerText = "Cash on Delivery (COD)";
//     if (document.getElementById("slip-order-status")) document.getElementById("slip-order-status").innerHTML = `<i class="fa fa-circle"></i> Pending & Confirmed`;
//
//     // 🏠 3. Addresses පිරවීම
//     if (document.getElementById("slip-billing-name")) document.getElementById("slip-billing-name").innerText = orderInfo.billing.name;
//     if (document.getElementById("slip-shipping-name")) document.getElementById("slip-shipping-name").innerText = orderInfo.billing.name;
//     if (document.getElementById("slip-shipping-address")) document.getElementById("slip-shipping-address").innerText = orderInfo.billing.address;
//     if (document.getElementById("slip-billing-details")) {
//         document.getElementById("slip-billing-details").innerHTML = `
//             ${orderInfo.billing.email}<br>
//             ${orderInfo.billing.mobile}<br>
//             ${orderInfo.billing.address}
//         `;
//     }
//
//     // ==========================================================
//     // 📦 4. DYNAMIC ITEMS LOOP WITH SAFETY CHECK
//     // ==========================================================
//     const tbody = document.getElementById("slip-items-tbody");
//
//     if (tbody) {
//         tbody.innerHTML = "";
//         const itemsList = orderInfo.cartItems || [];
//
//         if (itemsList.length > 0) {
//             itemsList.forEach(item => {
//                 // 🎯 Property mappings චෙක්අවුට් එකේ විදිහටම හැදුවා මචං
//                 let itemPrice = item.price || 0;
//                 let itemQty = item.qty || 1;
//                 let productTitle = item.productTitle || "Product Item";
//                 let categoryName = item.category || "Furniture"; // 'categoryName' වෙනුවට 'category'
//                 let itemTotal = itemPrice * itemQty;
//
//                 let row = `
//                     <tr>
//                         <td><img src="assets/images/product/product-1.jpg" width="50" alt="" class="border"></td>
//                         <td>
//                             <div class="fw-bold">${productTitle}</div>
//                             <small class="text-muted">Model: Dynamic | Status: Ordered</small>
//                         </td>
//                         <td>${categoryName}</td>
//                         <td class="text-center">${itemQty}</td>
//                         <td class="text-end">Rs. ${Number(itemPrice).toFixed(2)}</td>
//                         <td class="text-end">Rs. ${Number(itemTotal).toFixed(2)}</td>
//                     </tr>
//                 `;
//                 tbody.innerHTML += row;
//             });
//         } else {
//             tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">No items found in this order.</td></tr>`;
//         }
//     } else {
//         console.warn("⚠️ Warning: #slip-items-tbody wrapper not found in HTML!");
//     }
//
//     // ==========================================================
//     // 🎯 5. PRICING SUMMARY FIX (උඹේ කන්සෝල් ලොග් එකේ නම් වලටම හැදුවා මචං)
//     // ==========================================================
//     // 💡 කන්සෝල් එකේ තිබ්බේ: pricing: { subtotal, shipping, grandTotal }
//     const subtotalVal = orderInfo.prices ? orderInfo.prices.subtotal : 0;
//     const shippingVal = orderInfo.prices ? orderInfo.prices.shipping : 0;
//     const grandTotalVal = orderInfo.prices ? orderInfo.prices.grandTotal : 0;
//
//     if (document.getElementById("slip-subtotal")) {
//         document.getElementById("slip-subtotal").innerText = `Rs. ${Number(subtotalVal).toFixed(2)}`;
//     }
//     if (document.getElementById("slip-shipping")) {
//         document.getElementById("slip-shipping").innerText = `Rs. ${Number(shippingVal).toFixed(2)}`;
//     }
//     if (document.getElementById("slip-grand-total")) {
//         document.getElementById("slip-grand-total").innerText = `Rs. ${Number(grandTotalVal).toFixed(2)}`;
//     }
//
//     // 🔥 වැඩේ සර්වසම්පූර්ණ නිසා storage එක clean කරනවා
//     sessionStorage.removeItem("placed_order_info");
//     console.log("🎯 All available fields populated successfully without crashes!");
// });


document.addEventListener("DOMContentLoaded", function () {
    console.log("📊 Success Page Loaded! Processing Receipt...");



    // 📦 1. Storage එකෙන් ඩේටා කියවීම
    const storedOrderData = sessionStorage.getItem("placed_order_info");
    if (!storedOrderData) {
        console.error("❌ Error: No order data found in sessionStorage!");
        return;
    }

    const orderInfo = JSON.parse(storedOrderData);
    console.log("📦 Received Order Package:", orderInfo);

    const itemsList = orderInfo.cartItems || [];
    console.log("🐛 Debugging Products List Structure:", itemsList);

    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('orderId') || orderInfo.orderId || "INV-10";

    // 📅 2. Meta Data පිරවීම
    if (document.getElementById("slip-invoice-id")) document.getElementById("slip-invoice-id").innerText = `#${orderId}`;
    if (document.getElementById("slip-order-date")) {
        const today = new Date();
        document.getElementById("slip-order-date").innerText = today.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
    }

    // ==========================================================
    // 🎯 PAYMENT METHOD & STATUS DYNAMIC MATCH (Stripe vs COD)
    // ==========================================================
    let isStripe = orderInfo.paymentMethod === "Credit Card (Stripe)";

    if (document.getElementById("slip-payment-method")) {
        document.getElementById("slip-payment-method").innerText = isStripe ? "Credit Card (Stripe)" : "Cash on Delivery (COD)";
    }
    if (document.getElementById("slip-order-status")) {
        // 🔥 Stripe නම් Paid, COD නම් Pending කියලා වදිනවා මචං සිරාවටම!
        document.getElementById("slip-order-status").innerHTML = isStripe
            ? `<i class="fa fa-circle text-success"></i> Paid & Confirmed`
            : `<i class="fa fa-circle text-warning"></i> Pending & Confirmed`;
    }

    // 🏠 3. Addresses පිරවීම (Stripe එකේදී billing නැත්නම් default වැටෙන්න safety එකක් දැම්මා)
    const billingName = orderInfo.billing ? orderInfo.billing.name : "Valued Customer";
    const billingEmail = orderInfo.billing ? orderInfo.billing.email : "customer@ninewoods.com";
    const billingMobile = orderInfo.billing ? orderInfo.billing.mobile : "N/A";
    const billingAddress = orderInfo.billing ? orderInfo.billing.address : "Registered Delivery Address";

    if (document.getElementById("slip-billing-name")) document.getElementById("slip-billing-name").innerText = billingName;
    if (document.getElementById("slip-shipping-name")) document.getElementById("slip-shipping-name").innerText = billingName;
    if (document.getElementById("slip-shipping-address")) document.getElementById("slip-shipping-address").innerText = billingAddress;
    if (document.getElementById("slip-billing-details")) {
        document.getElementById("slip-billing-details").innerHTML = `
            ${billingEmail}<br>
            ${billingMobile}<br>
            ${billingAddress}
        `;
    }

    // ==========================================================
    // 📦 4. DYNAMIC ITEMS LOOP WITH SAFETY CHECK
    // ==========================================================
    const tbody = document.getElementById("slip-items-tbody");
    let calculatedSubtotal = 0;
    let hasHeavyItem = false;

    if (tbody) {
        tbody.innerHTML = "";
        const itemsList = orderInfo.cartItems || [];

        if (itemsList.length > 0) {
            itemsList.forEach(item => {
                let itemPrice = item.price || 0;
                let itemQty = item.qty || 1;
                let productTitle = item.productTitle || "Product Item";
                let categoryName = item.category || "Furniture";
                let itemTotal = itemPrice * itemQty;

                calculatedSubtotal += itemTotal;
                if (item.isHeavy === true) {
                    hasHeavyItem = true;
                }

                let row = `
                    <tr>
                        <td><img src="assets/images/product/product-1.jpg" width="50" alt="" class="border"></td>
                        <td>
                            <div class="fw-bold">${productTitle}</div>
                            <small class="text-muted">Status: Verified</small>
                        </td>
                        <td>${categoryName}</td>
                        <td class="text-center">${itemQty}</td>
                        <td class="text-end">Rs. ${Number(itemPrice).toFixed(2)}</td>
                        <td class="text-end">Rs. ${Number(itemTotal).toFixed(2)}</td>
                    </tr>
                `;
                tbody.innerHTML += row;
            });
        } else {
            tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">No items found in this order.</td></tr>`;
        }
    }

    // ==========================================================
    // 🎯 5. PRICING SUMMARY DYNAMIC CALCULATIONS (Rs. වලින්)
    // ==========================================================
    // 💡 COD එකේ නම් pricing object එකෙන් ගනී, Stripe එකේදී අපිට direct ඩයිනමික් හදලා දෙයි ලෙඩ නැති වෙන්න
    let subtotalVal = orderInfo.prices ? orderInfo.prices.subtotal : calculatedSubtotal;
    let shippingVal = orderInfo.prices ? orderInfo.prices.shipping : (hasHeavyItem ? 5000.00 : 500.00);
    let grandTotalVal = orderInfo.prices ? orderInfo.prices.grandTotal : (subtotalVal + shippingVal);

    if (document.getElementById("slip-subtotal")) {
        document.getElementById("slip-subtotal").innerText = `Rs. ${Number(subtotalVal).toFixed(2)}`;
    }
    if (document.getElementById("slip-shipping")) {
        document.getElementById("slip-shipping").innerText = `Rs. ${Number(shippingVal).toFixed(2)}`;
    }
    if (document.getElementById("slip-grand-total")) {
        document.getElementById("slip-grand-total").innerText = `Rs. ${Number(grandTotalVal).toFixed(2)}`;
    }

    // 🔥 වැඩේ සර්වසම්පූර්ණ නිසා storage එක clean කරනවා
    sessionStorage.removeItem("placed_order_info");
    console.log("🎯 All available fields populated successfully without crashes!");
});