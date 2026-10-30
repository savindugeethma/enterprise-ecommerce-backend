window.addEventListener("load", async () => {
    Notiflix.Loading.pulse("Data is loading", {
        clickToClose: false,
        svgColor: '#0284c7'
    });
    try {
        // await loadProducts();

        await loadAdminDashboardData();
        // await logSellerProducts();
        // await logSellerCustomers();

    } finally {
        Notiflix.Loading.remove();
    }
});

async function loadAdminDashboardData1() {
    try {
        console.log("Fetching admin dashboard overview data...");

        const response = await fetch('api/admin-profile/overview');

        if (response.status === 401) {
            console.log("Unauthorized access! Redirecting to login/error page...");
            window.location.href = '/ninewood/unauthorized.html';
            return;
        }

        if (response.ok) {
            const data = await response.json();
            console.log("---- ADMIN API RESPONSE ----");
            console.log(data);
            console.log("----------------------------");

            if (data.status) {
                document.getElementById("admin-total-revenue").innerText = `$ ${data.totalRevenue.toLocaleString()}`;
                document.getElementById("admin-total-orders").innerText = data.totalOrders;
                document.getElementById("admin-total-customers").innerText = data.totalCustomers;

                renderAdminOrders(data.recentOrders);
            } else {
                console.error("API Error: " + data.message);
            }
        } else {
            console.error("HTTP Request Failed with Status: " + response.status);
        }
    } catch (e) {
        console.error("Error loading admin dashboard data:", e);
    }
}

// async function logSellerProducts() {
//     try {
//         console.log("Loading Product Report");
//
//         const response = await fetch('api/admin-profile/products');
//         if (response.status === 401) {
//             console.log("Unauthorized access! Redirecting to login/error page...");
//             window.location.href = '/ninewood/unauthorized.html';
//             return;
//         }
//         if (response.ok) {
//             const data = await response.json();
//
//             // console.log("📦 ==== SELLER PRODUCTS LIST ====");
//             console.log(data);
//
//
//             if (data.status) {
//                 console.log(`Total Products Found: ${data.products.length}`);
//
//                 renderProductReport(data);
//
//                 data.products.forEach(product => {
//                     console.log(`Product ID: #${product.id} | Title: ${product.title} | Price: $${product.price} | Qty: ${product.qty} | Status: ${product.status}`);
//                 });
//             } else {
//                 console.log("API Product Error: " + data.message);
//             }
//             console.log("=================================");
//         } else {
//             console.log("HTTP Request Failed for Products with Status: " + response.status);
//         }
//     } catch (e) {
//         console.error("Error logging products in JS:", e);
//     }
// }

// async function logSellerCustomers() {
//     try {
//         console.log("Loading Customers Report");
//
//         const response = await fetch('api/admin-profile/customers');
//         if (response.ok) {
//             const data = await response.json();
//
//             console.log("👥 ==== SELLER CUSTOMERS LIST ====");
//             console.log(data);
//
//             if (data.status) {
//                 console.log(`Total Customers Found: ${data.customers.length}`);
//
//                 renderCustomersReport(data);
//                 data.customers.forEach(customer => {
//                     console.log(`Customer ID: #${customer.id} | Name: ${customer.name} | Email: ${customer.email}`);
//                 });
//
//             } else {
//                 console.log("API Customer Error: " + data.message);
//             }
//             console.log("==================================");
//         } else {
//             console.log("HTTP Request Failed for Customers with Status: " + response.status);
//         }
//     } catch (e) {
//         console.error("Error logging customers in JS:", e);
//     }
// }

async function renderProductReport(products){
    if (document.getElementById("rep-total-catalog")) {
        let totalItems = products ? products.length : 0;
        let totalAssetValue = 0;
        let totalPriceSum = 0;
        let outOfStockCount = 0;

        if (totalItems > 0) {
            products.forEach(product => {
                totalAssetValue += (product.price * product.qty);
                totalPriceSum += product.price;

                if (product.qty <= 0) {
                    outOfStockCount++;
                }
            });
        }

        let avgPrice = totalItems > 0 ? (totalPriceSum / totalItems) : 0.00;

        document.getElementById("rep-total-catalog").innerText = totalItems;
        document.getElementById("rep-total-asset-value").innerText = `$${totalAssetValue.toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
        document.getElementById("rep-avg-price").innerText = `$${avgPrice.toFixed(2)}`;
        document.getElementById("rep-out-of-stock").innerText = outOfStockCount === 0 ? "0 Items" : `${outOfStockCount} Items`;

        const alertEl = document.getElementById("rep-out-of-stock");
        if (outOfStockCount > 0) {
            alertEl.style.color = "#dc3545";
        } else {
            alertEl.style.color = "inherit";
        }
    }

    const reportTableBody = document.getElementById("product-report-table-body");

    if (!reportTableBody) {
        console.error("❌ Error: Report Table Body (#product-report-table-body) හම්බවුණේ නැහැ මචං!");
        return;
    }

    reportTableBody.innerHTML = "";

    if (!products || products.length === 0) {
        reportTableBody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-muted">No product data available for this report.</td></tr>`;
        return;
    }

    products.forEach(product => {

        let totalAssetValue = product.price * product.qty;

        let badgeClass = "bg-instock";
        let stockText = `${product.qty} In Stock`;

        if (product.qty <= 0) {
            badgeClass = "bg-outstock";
            stockText = "Out of Stock";
        } else if (product.qty <= 5) {
            badgeClass = "bg-lowstock";
            stockText = `${product.qty} Low Stock`;
        }

        let category = product.category ? product.category : "Furniture";
        let imageSrc = product.image ? product.image : "assets/images/product/product-1.jpg";

        let row = `
            <tr>
                <td><strong>#PROD-${product.id}</strong></td>
                <td><img src="${imageSrc}" width="40" alt="" class="border"></td>
                <td><strong>${product.title}</strong></td>
                <td>${category}</td>
                <td>$${product.price.toFixed(2)}</td>
                <td class="text-center">
                    <span class="badge-status ${badgeClass}">${stockText}</span>
                </td>
                <td class="text-end"><strong>$${totalAssetValue.toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2})}</strong></td>
            </tr>
        `;

        reportTableBody.innerHTML += row;
    });

    console.log("🎯 Premium Product Report Table successfully rendered!");
}

async function renderCustomersReport(customers) {

    if (document.getElementById("rep-total-customers")) {
        let totalCount = customers ? customers.length : 0;

        let activeCount = Math.round(totalCount * 0.75);
        let activityRate = totalCount > 0 ? ((activeCount / totalCount) * 100).toFixed(1) : "0.0";

        let newThisMonth = Math.round(totalCount * 0.12);
        let avgPerDay = (newThisMonth / 30).toFixed(1);

        let avgLTV = totalCount > 0 ? 345.50 : 0.00;

        document.getElementById("rep-total-customers").innerText = totalCount;
        document.getElementById("rep-active-customers").innerText = activeCount;
        document.getElementById("rep-activity-rate").innerText = activityRate;
        document.getElementById("rep-new-customers").innerText = newThisMonth;
        document.getElementById("rep-avg-per-day").innerText = avgPerDay;
        document.getElementById("rep-avg-ltv").innerText = `$${avgLTV.toFixed(2)}`;
    }

    const reportTableBody = document.getElementById("customer-report-table-body");

    if (!reportTableBody) {
        console.error("❌ Error: Report Table Body (#customer-report-table-body) හම්බවුණේ නැහැ මචං!");
        return;
    }

    reportTableBody.innerHTML = "";

    if (!customers || customers.length === 0) {
        reportTableBody.innerHTML = `<tr><td colspan="4" class="text-center py-4 text-muted">No customer data available for this report.</td></tr>`;
        return;
    }

    customers.forEach((customer, index) => {

        let joinDate = customer.joinDate ? customer.joinDate : "N/A";
        let orderCount = customer.orderCount ? customer.orderCount : "01";
        let totalSpent = customer.totalSpent ? customer.totalSpent.toFixed(2) : "120.00";

        let badgeClass = "bg-active";
        let statusText = "Active";
        if (index === 0) {
            badgeClass = "bg-vip";
            statusText = "VIP";
        }

        let row = `
            <tr>
                <td><strong>#CUST-${customer.id}</strong></td>
                <td><strong>${customer.name}</strong></td>
                <td>${customer.email}</td>
                <td>${joinDate}</td>
                <td class="text-center">${orderCount}</td>
                <td class="text-center"><span class="badge-status ${badgeClass}">${statusText}</span></td>
                <td class="text-end"><strong>$ ${totalSpent}</strong></td>
            </tr>
        `;

        reportTableBody.innerHTML += row;
    });

    console.log("🎯 Premium Customer Report Table successfully rendered!");
}

async function renderSalesReport(allOrders) {
    const topSellingBody = document.getElementById("top-selling-table-body");
    const orderLogBody = document.getElementById("sales-report-orders-tbody");

    if (!orderLogBody || !topSellingBody) {
        console.error("❌ Error: Table Bodies හම්බවුණේ නැහැ මචං!");
        return;
    }

    orderLogBody.innerHTML = "";
    topSellingBody.innerHTML = "";

    if (!allOrders || allOrders.length === 0) {
        orderLogBody.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-muted">No order data available for this report.</td></tr>`;
        topSellingBody.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-muted">No top selling products.</td></tr>`;
        return;
    }

    // ==========================================================
    // 📊 FRONT-END CALCULATIONS
    // ==========================================================
    let monthlyRevenue = 0;
    let completedOrdersCount = 0;
    let productSalesMap = {};

    allOrders.forEach(order => {
        let currentStatus = order.status ? order.status.toString().trim().toLowerCase() : "";

        // 💰 හැම ඕඩර් එකකම total එක එකතු කරනවා (උඹේ කන්සෝල් එකේ 'total: 2000' තියෙනවා)
        monthlyRevenue += order.total;
        completedOrdersCount++;

        // 📦 Top Selling Dummy Grouping
        let mockProduct = order.id % 2 === 0 ? "Modern Living Chair" : "Herman Arm Chair";
        let mockCategory = "Furniture";

        if (!productSalesMap[mockProduct]) {
            productSalesMap[mockProduct] = { name: mockProduct, category: mockCategory, count: 0, earnings: 0 };
        }
        productSalesMap[mockProduct].count += 1;
        productSalesMap[mockProduct].earnings += order.total;
    });

    let avgOrderValue = completedOrdersCount > 0 ? (monthlyRevenue / completedOrdersCount) : 0.00;

    // 🎯 KPI Cards පිරවීම
    if (document.getElementById("rep-sales-revenue")) {
        document.getElementById("rep-sales-revenue").innerText = `$ ${monthlyRevenue.toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
        document.getElementById("rep-sales-completed").innerText = completedOrdersCount;
        document.getElementById("rep-sales-aov").innerText = `$ ${avgOrderValue.toFixed(2)}`;
        document.getElementById("rep-sales-conversion").innerText = "4.25%";
    }

    // ==========================================================
    // 🏆 TABLE 1: TOP SELLING PRODUCTS
    // ==========================================================
    let sortedProducts = Object.values(productSalesMap).sort((a, b) => b.count - a.count);
    sortedProducts.forEach((prod, idx) => {
        let row = `
            <tr>
                <td><strong>${idx + 1}</strong></td>
                <td><strong>${prod.name}</strong></td>
                <td>${prod.category}</td>
                <td class="text-center">${prod.count}</td>
                <td class="text-end"><strong>$ ${prod.earnings.toLocaleString(undefined, {minimumFractionDigits: 2, maximumFractionDigits: 2})}</strong></td>
            </tr>
        `;
        topSellingBody.innerHTML += row;
    });

    // ==========================================================
    // 📝 TABLE 2: LAST MONTH ORDER DETAILS
    // ==========================================================
    allOrders.forEach(order => {
        let badgeClass = "bg-pending";
        let currentStatus = order.status ? order.status.toString().trim().toLowerCase() : "";

        if (currentStatus === "completed" || currentStatus === "success" || currentStatus === "active") {
            badgeClass = "bg-completed";
        } else if (currentStatus === "cancelled" || currentStatus === "failed") {
            badgeClass = "bg-cancelled";
        }

        let row = `
            <tr>
                <td><strong>#ORD-${order.id}</strong></td>
                <td>${order.customer}</td>
                <td>${order.createdAt.split('T')[0]}</td> <td><span class="badge-status ${badgeClass}">${order.status}</span></td>
                <td class="text-end"><strong>$ ${order.total.toFixed(2)}</strong></td>
            </tr>
        `;
        orderLogBody.innerHTML += row;
    });
}

//rendering
// async function loadAdminDashboardData() {
//     try {
//         console.log("Fetching admin dashboard overview data...");
//         const response = await fetch('api/admin-profile/overview');
//
//         if (response.ok) {
//             const data = await response.json();
//             console.log("---- OVERVIEW RESPONSE ----", data);
//
//             if (data.status) {
//                 // Cards ටික පිරවීම
//                 document.getElementById("admin-total-revenue").innerText = `$ ${data.totalRevenue.toLocaleString()}`;
//                 document.getElementById("admin-total-orders").innerText = data.totalOrders;
//                 document.getElementById("admin-total-customers").innerText = data.totalCustomers;
//
//                 // Recent Orders ටේබල් එක පිරවීම
//                 const tableBody = document.getElementById("admin-recent-orders-table-body");
//                 if (!tableBody) return;
//                 tableBody.innerHTML = "";
//
//                 if (data.recentOrders.length === 0) {
//                     tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-3">No recent orders found.</td></tr>`;
//                     return;
//                 }
//
//                 data.recentOrders.forEach(order => {
//                     let badgeColor = "bg-warning text-dark";
//                     let currentStatus = order.status ? order.status.toString().trim().toLowerCase() : "";
//
//                     if (currentStatus === "approved" || currentStatus === "success" || currentStatus === "active" || currentStatus === "completed") {
//                         badgeColor = "bg-success text-white";
//                     } else if (currentStatus === "on hold" || currentStatus === "processing") {
//                         badgeColor = "bg-info text-dark";
//                     }
//
//                     let row = `
//                         <tr>
//                             <td><strong>#ORD-${order.id}</strong></td>
//                             <td>${order.customer}</td>
//                             <td>${order.createdAt}</td>
//                             <td><span class="badge ${badgeColor}" style="padding: 5px 10px; font-size: 0.75rem;">${order.status}</span></td>
//                             <td><strong>$ ${order.total}</strong></td>
//                             <td>
//                                 <button class="btn" style="background-color: #f2a100; color: white; font-weight: 600; font-size: 0.7rem; padding: 2px 10px; border: none; border-radius: 4px;" onclick="viewAdminOrderDetails(${order.id})">Details</button>
//                                 <button class="btn" style="background-color: #f2a100; color: white; font-weight: 600; font-size: 0.7rem; padding: 2px 10px; border: none; border-radius: 4px;" onclick="viewAdminOrderDetails(${order.id})">Delivered</button>
//
//                             </td>
//                         </tr>
//                     `;
//                     tableBody.innerHTML += row;
//                 });
//             }
//         }
//     } catch (e) {
//         console.error("Error loading overview data:", e);
//     }
// }



async function loadSellerProducts() {
    const tableBody = document.getElementById("seller-products-table-body");
    if (!tableBody) return;
    tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-3">Loading products...</td></tr>`;

    try {
        const response = await fetch('api/admin-profile/products');
        if (response.ok) {
            const data = await response.json();
            console.log("---- PRODUCTS RESPONSE ----", data);

            tableBody.innerHTML = ""; // Clear loading

            if (data.status && data.products.length > 0) {
                data.products.forEach(product => {
                    let badgeColor = product.status.toLowerCase() === 'active' ? 'bg-success text-white' : 'bg-danger text-white';

                    let row = `
                        <tr>
                            <td><strong>#PRD-${product.id}</strong></td>
                            <td>${product.title}</td>
                            <td><strong>$ ${product.price}</strong></td>
                            <td>${product.qty} Items</td>
                            <td><span class="badge ${badgeColor}" style="padding: 5px 10px; font-size: 0.75rem;">${product.status}</span></td>
                            <td>
                                <a href="#" class="text-primary me-2" onclick="editProduct(${product.id})"><i class="fa fa-edit"></i></a>
                                <a href="#" class="text-danger" onclick="deleteProduct(${product.id})"><i class="fa fa-trash"></i></a>
                            </td>
                        </tr>
                    `;
                    tableBody.innerHTML += row;
                });
            } else {
                tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-4">No products found!</td></tr>`;
            }
        }
    } catch (e) {
        console.error("Error loading products:", e);
    }
}

async function loadSellerCustomers() {
    const tableBody = document.getElementById("seller-customers-table-body");
    if (!tableBody) return;
    tableBody.innerHTML = `<tr><td colspan="4" class="text-center py-3">Loading customers...</td></tr>`;

    try {
        const response = await fetch('api/admin-profile/customers');
        if (response.ok) {
            const data = await response.json();
            console.log("---- CUSTOMERS RESPONSE ----", data);

            tableBody.innerHTML = ""; // Clear loading

            if (data.status && data.customers.length > 0) {
                data.customers.forEach((customer, index) => {
                    let row = `
                        <tr>
                            <td>#CUST-${customer.id}</td>
                            <td><strong>${customer.name}</strong></td>
                            <td>${customer.email}</td>
                            <td>
                                <button class="btn btn-sm btn-outline-dark" style="font-size: 0.7rem; padding: 2px 8px;" onclick="viewCustomerHistory(${customer.id})">History</button>
                            </td>
                        </tr>
                    `;
                    tableBody.innerHTML += row;
                });
            } else {
                tableBody.innerHTML = `<tr><td colspan="4" class="text-center py-4">No buyers found.</td></tr>`;
            }
        }
    } catch (e) {
        console.error("Error loading customers:", e);
    }
}



document.addEventListener("DOMContentLoaded", function () {
    loadAdminDashboardData();

    const tabElements = document.querySelectorAll('button[data-bs-toggle="pill"], a[data-bs-toggle="pill"]');
    tabElements.forEach(tab => {
        tab.addEventListener('shown.bs.tab', function (event) {
            const targetId = event.target.getAttribute('href') || event.target.getAttribute('data-bs-target');
            console.log("Tab Switched to:", targetId);

            if (targetId === "#pills-products") {
                loadSellerProducts();
            } else if (targetId === "#pills-customers") {
                loadSellerCustomers();
            } else if (targetId === "#pills-dashboard") {
                loadAdminDashboardData();
            }
        });
    });
});

window.onload = function() {
    // loadAdminDashboardData();
    // logSellerProducts();
    // logSellerCustomers();
};

async function addProduct() {
    try {
        alert("🔥 JS Triggered Successfully!");

        const formData = new FormData();


        const nameInput = document.getElementById("product-name");
        const categoryInput = document.getElementById("product-category");
        const categoryId = categoryInput ? categoryInput.value : "";
        const priceInput = document.getElementById("product-price");
        const qtyInput = document.getElementById("product-stock-qty");
        const colourInput = document.getElementById("product-colour");
        const modelInput = document.getElementById("product-model");
        const descInput = document.getElementById("product-description");
        const isHeavyInput = document.getElementById("product-is-heavy");


        if (!nameInput || !nameInput.value || !priceInput || !priceInput.value || !qtyInput || !qtyInput.value) {
            if (typeof Notiflix !== 'undefined') {
                Notiflix.Notify.warning("Please fill required fields (Name, Price, Qty)!");
            } else {
                alert("Please fill required fields (Name, Price, Qty)!");
            }
            return;
        }

        formData.append("name", nameInput.value.trim());
        formData.append("category", categoryInput ? categoryInput.value : "1");
        formData.append("price", priceInput.value);
        formData.append("qty", qtyInput.value);
        // formData.append("colour", colourInput ? colourInput.value.trim() : "");
        formData.append("color", colourInput ? colourInput.value.trim() : "1");
        formData.append("model", modelInput ? modelInput.value.trim() : "");
        formData.append("description", descInput ? descInput.value.trim() : "");
        formData.append("category", categoryId);

        const isHeavy = isHeavyInput ? isHeavyInput.checked : false;
        formData.append("isHeavy", isHeavy);

        const fileInput1 = document.getElementById("product-image-file-1");
        const fileInput2 = document.getElementById("product-image-file-2");

        if (fileInput1 && fileInput1.files.length > 0) {
            formData.append("image1", fileInput1.files[0]);
        }
        if (fileInput2 && fileInput2.files.length > 0) {
            formData.append("image2", fileInput2.files[0]);
        }

        if (typeof Notiflix !== 'undefined') {
            Notiflix.Loading.pulse("Saving your new product to database...", {
                svgColor: '#0284c7',
                clickToClose: false
            });
        }


        const response = await fetch("api/admin/add-product", {
            method: "POST",
            body: formData
        });

        if (response.ok) {
            const data = await response.json();

            if (data.status) {
                if (typeof Notiflix !== 'undefined') {
                    Notiflix.Notify.success("Product Saved Successfully! 🎉");
                } else {
                    alert("Product Saved Successfully!");
                }


                setTimeout(() => {
                    window.location.reload();
                }, 1500);

            } else {
                if (typeof Notiflix !== 'undefined') Notiflix.Notify.failure("Error: " + data.message);
                else alert("Failed: " + data.message);
            }
        } else {
            console.error("HTTP Error Status:", response.status);
            if (typeof Notiflix !== 'undefined') Notiflix.Notify.failure("Server responded with error status!");
        }

    } catch (e) {
        console.error("Critical Add Product JS Error:", e);
        if (typeof Notiflix !== 'undefined') Notiflix.Notify.failure("Something went wrong!");
    } finally {

        if (typeof Notiflix !== 'undefined') Notiflix.Loading.remove();
    }
}

// async function loadProducts(){
//     try {
//         // console.log("done1");
//         const response = await fetch("api/admin-profile/admin-products");
//         if (response.ok) {
//             // console.log("done2");
//             const data = await response.json();
//             // document.getElementById("addName").innerHTML = `Name: ${data.name}`;
//             // document.getElementById("addEmail").innerHTML = `Email: ${data.email}`;
//             console.log(data);
//             renderProducts(data.products);
//             // console.log("response.ok");
//
//         }
//     } catch (e) {
//         Notiflix.Notify.failure(e.message, {
//             position: 'center-top'
//         });
//     } finally {
//         Notiflix.Loading.remove();
//     }
// }
//
// function renderProducts(products){
//
// }
//
// async function loadOrders(){
//     try {
//         console.log("done1");
//         const response = await fetch("api/admin-profile/admin-orders");
//         if (response.ok) {
//             // console.log("done2");
//             const data = await response.json();
//             console.log(data);
//             renderOrders(data.orders);
//             // console.log("response.ok");
//
//         }
//     } catch (e) {
//         Notiflix.Notify.failure(e.message, {
//             position: 'center-top'
//         });
//     } finally {
//         Notiflix.Loading.remove();
//     }
// }
//
// function renderOrders(orders){
//
// }
//
// async function loadCustomers(){
//     try {
//         console.log("loadCustomers()");
//         const responseC = await fetch("api/admin-profile/admin-customers");
//         if (responseC.ok) {
//             // console.log("done2");
//             const customers = await responseC.json();
//             console.log(customers);
//             // renderProducts(customers.products);
//             // console.log("response.ok");
//
//         }
//     } catch (e) {
//         Notiflix.Notify.failure(e.message, {
//             position: 'center-top'
//         });
//     } finally {
//         Notiflix.Loading.remove();
//     }
// }
//
// function renderCustomers(customers){
// }





let globalRecentOrders = [];


async function loadAdminDashboardData() {
    try {
        console.log("Fetching admin dashboard overview data...");
        const response = await fetch('api/admin-profile/overview');

        if (response.ok) {
            const data = await response.json();
            console.log("---- OVERVIEW RESPONSE ----", data);

            if (data.status) {

                globalRecentOrders = data.recentOrders || [];


                if (document.getElementById("admin-total-revenue")) {
                    document.getElementById("admin-total-revenue").innerText = `$ ${data.totalRevenue ? data.totalRevenue.toLocaleString() : '0'}`;
                }
                if (document.getElementById("admin-total-orders")) {
                    document.getElementById("admin-total-orders").innerText = data.totalOrders || 0;
                }
                if (document.getElementById("admin-total-customers")) {
                    document.getElementById("admin-total-customers").innerText = data.totalCustomers || 0;
                }

                // Recent Orders ටේබල් එක පිරවීම
                const tableBody = document.getElementById("admin-recent-orders-table-body");
                if (!tableBody) return;
                tableBody.innerHTML = "";

                if (globalRecentOrders.length === 0) {
                    tableBody.innerHTML = `<tr><td colspan="6" class="text-center py-3">No recent orders found.</td></tr>`;
                    return;
                }

                globalRecentOrders.forEach(order => {
                    let badgeColor = "bg-warning text-dark";
                    let currentStatus = order.status ? order.status.toString().trim().toLowerCase() : "";

                    if (currentStatus === "approved" || currentStatus === "success" || currentStatus === "active" || currentStatus === "completed" || currentStatus === "delivered") {
                        badgeColor = "bg-success text-white";
                    } else if (currentStatus === "on hold" || currentStatus === "processing") {
                        badgeColor = "bg-info text-dark";
                    } else if (currentStatus === "cancelled") {
                        badgeColor = "bg-danger text-white";
                    }

                    let row = `
                        <tr>
                            <td><strong>#ORD-${order.id}</strong></td>
                            <td>${order.customer || 'N/A'}</td>
                            <td>${order.createdAt ? order.createdAt.replace('T', ' ') : 'N/A'}</td>
                            <td><span class="badge ${badgeColor}" style="padding: 5px 10px; font-size: 0.75rem;">${order.status}</span></td>
                            <td><strong>$ ${Number(order.total || 0).toLocaleString()}</strong></td>
                            <td>
                                <button class="btn" style="background-color: #f2a100; color: white; font-weight: 600; font-size: 0.7rem; padding: 2px 10px; border: none; border-radius: 4px; cursor: pointer;" onclick="viewAdminOrderDetails(${order.id})">Details</button>
                                <button class="btn" style="background-color: #28a745; color: white; font-weight: 600; font-size: 0.7rem; padding: 2px 10px; border: none; border-radius: 4px; cursor: pointer;" onclick="markOrderDelivered(${order.id})">Mark As Shipped</button>
                            </td>                  
                        </tr>
                    `;
                    tableBody.innerHTML += row;
                });
            }
        }
    } catch (e) {
        console.error("Error loading overview data:", e);
    }
}


window.viewAdminOrderDetails = function(orderId) {
    const order = globalRecentOrders.find(o => o.id === orderId);

    if (!order) {
        console.error("Order not found in cache for ID:", orderId);
        return;
    }

    // Modal UI Feeding data
    document.getElementById('manage-order-id').value = order.id;
    document.getElementById('modal-order-id').innerText = `#ORD-${order.id}`;
    document.getElementById('modal-order-date').innerText = `Placed on: ${order.createdAt ? order.createdAt.replace('T', ' ') : 'N/A'}`;

    // Customer Info
    document.getElementById('modal-customer-name').innerText = order.customer || 'N/A';
    document.getElementById('modal-customer-email').innerText = order.customerEmail || 'savindu@example.com';
    document.getElementById('modal-customer-phone').innerText = order.customerPhone || 'N/A';
    document.getElementById('modal-shipping-address').innerText = order.shippingAddress || 'Standard Delivery Address';

    // Status & Total
    document.getElementById('modal-order-status').value = order.status || 'PENDING';
    document.getElementById('modal-tracking-number').value = order.trackingNumber || '';
    document.getElementById('modal-order-total').innerText = `$ ${Number(order.total || 0).toLocaleString()}`;

    // Items List Render
    let itemsHtml = '';
    if (order.items && order.items.length > 0) {
        order.items.forEach(item => {
            itemsHtml += `
                <tr style="border-bottom: 1px solid #f0f0f0;">
                    <td style="padding: 8px 12px;"><strong>${item.title || item.name}</strong></td>
                    <td style="padding: 8px 12px; text-align: center;">${item.qty || 1}</td>
                    <td style="padding: 8px 12px; text-align: right;">$ ${Number(item.price || order.total).toLocaleString()}</td>
                    <td style="padding: 8px 12px; text-align: right; font-weight: 600;">$ ${Number(item.subtotal || order.total).toLocaleString()}</td>
                </tr>
            `;
        });
    } else {
        itemsHtml = `
            <tr style="border-bottom: 1px solid #f0f0f0;">
                <td style="padding: 8px 12px;">Order Summary Package</td>
                <td style="padding: 8px 12px; text-align: center;">1</td>
                <td style="padding: 8px 12px; text-align: right;">$ ${Number(order.total).toLocaleString()}</td>
                <td style="padding: 8px 12px; text-align: right; font-weight: 600;">$ ${Number(order.total).toLocaleString()}</td>
            </tr>
        `;
    }
    document.getElementById('modal-order-items-table').innerHTML = itemsHtml;

    const modal = document.getElementById('order-details-modal');
    if (modal) {
        modal.style.display = 'flex';
    }
};

// 4. Modal Close func
window.closeOrderModal = function() {
    const modal = document.getElementById('order-details-modal');
    if (modal) {
        modal.style.display = 'none';
    }
};

// 5. Delivered Button
window.markOrderDelivered = async function(orderId) {
    if (!confirm(`Are you sure you want to mark Order #ORD-${orderId} as Delivered?`)) return;

    try {
        const response = await fetch(`api/admin-profile/update-order-details`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ orderId: orderId, status: 'DELIVERED' })
        });
        const res = await response.json();
        if (res.status) {
            alert("Order status updated to Delivered!");
            loadAdminDashboardData(); // Table එක Refresh කිරීම
        } else {
            alert(res.message || "Failed to update status");
        }
    } catch (e) {
        console.error("Error marking delivered:", e);
    }
};

// 6. Modal එකෙන් Status & Tracking Save කරන Function එක
window.updateAdminOrder = async function() {

    console.log("btn clicked");
    const orderId = document.getElementById('manage-order-id').value;
    const status = document.getElementById('modal-order-status').value;
    const trackingNumber = document.getElementById('modal-tracking-number').value.trim();

    console.log(orderId, "order status : " ,status, "tracking number : ", trackingNumber);

    const params = new URLSearchParams();
    params.append("orderId", orderId);
    params.append("status", status);
    params.append("trackingNumber", trackingNumber);

    console.log(orderId, "order status : " ,status, "tracking number : ", trackingNumber);

    try {
        const response = await fetch(`api/admin-profile/update-order-details`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params
        });

        if (response.ok) {
            const res = await response.json();
            if (res.status) {
                alert(res.message || "Order updated successfully!");
                closeOrderModal();
                loadAdminDashboardData(); // Refresh overview table
            } else {
                alert(res.message || "Update failed");
            }
        } else {
            alert("Server error occurred while updating the order.");
        }
    } catch (err) {
        console.error("Error updating order:", err);
    }
};

// Page load වෙද්දී Dashboard data load කිරීම
// document.addEventListener('DOMContentLoaded', () => {
//     loadAdminDashboardData();
// });