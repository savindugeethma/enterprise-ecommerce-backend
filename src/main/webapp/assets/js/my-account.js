window.addEventListener("load", async () => {
    Notiflix.Loading.pulse("Data is loading", {
        clickToClose: false,
        svgColor: '#0284c7'
    });
    try {
        // await getCities();
        await loadUserData();
        await loadAddress();
        await loadUserOrders();

    } finally {
        Notiflix.Loading.remove();
    }
});

// document.getElementById("address-anchor").addEventListener("click", async () => {
//     await loadAddress();
// });

async function loadUserOrders() {
    try {
        console.log("Fetching order history from API...");

        const response = await fetch('api/checkout/order-history');
        if (response.ok) {
            const data = await response.json();

            console.log("---- API RESPONSE DATA ----");
            console.log(data); // මුළු JSON එකම එකපාර බලාගන්න
            console.log("---------------------------");

            if (data.status) {
                console.log("Orders Array inside response:");
                // ඇතුළේ තියෙන orders ටික එකින් එක පේළියෙන් පේළියට කන්සෝල් එකේ ලොග් කරනවා
                data.orders.forEach(order => {
                    console.log(`Order ID: #ORD-${order.id} | Date: ${order.createdAt} | Status: ${order.status}`);
                });

                renderOrders(data.orders);

            } else {
                console.log("API Error Message: " + data.message);
            }
        } else {
            console.log("HTTP Request Failed with Status: " + response.status);
        }
    } catch (e) {
        console.error("Error loading dashboard orders in JS:", e);
    }
}

document.addEventListener("DOMContentLoaded", loadUserOrders);

function renderOrders(orders) {
    const orderTableBody = document.getElementById("user-orders-table-body");

    if (!orderTableBody) {
        console.error("❌ HTML Table body (#user-orders-table-body) ");
        return;
    }

    orderTableBody.innerHTML = "";

    if (orders.length === 0) {
        orderTableBody.innerHTML = `<tr><td colspan="6" class="text-center py-4">You haven't placed any orders yet.</td></tr>`;
        return;
    }

    orders.forEach((order, index) => {
        let badgeColor = "bg-warning text-dark";
        let currentStatus = order.status ? order.status.toString().trim().toLowerCase() : "";

        if (currentStatus === "approved" || currentStatus === "success" || currentStatus === "active") {
            badgeColor = "bg-success text-white";
        } else if (currentStatus === "on hold" || currentStatus === "processing") {
            badgeColor = "bg-info text-dark";
        }

        let pName = order.name ? order.name : "Furniture Item";

        let row = `
            <tr>
                <td>${index + 1}</td>
                <td>${pName}</td>
                <td>${order.createdAt}</td>
                <td>
                    <span class="badge ${badgeColor}" style="padding: 6px 12px; font-size: 0.8rem; border-radius: 4px;">
                        ${order.status}
                    </span>
                </td>
                <td><strong>$ ${order.total}</strong></td>
<!--                <td>-->
<!--                    <a href="#" class="btn btn-sm btn-outline-warning" style="font-size: 0.8rem; padding: 2px 10px;" onclick="viewOrderDetails(${order.id})">View</a>-->
<!--                </td>-->
                    <td>
                        <button class="btn" 
                                style="background-color: #f39c12; color: white; font-weight: 600; font-size: 0.7rem; padding: 2px 10px; line-height: 1.2; border-radius: 4px; border: none; text-transform: uppercase; letter-spacing: 0.5px; transition: 0.3s; height: auto;" 
                                onmouseover="this.style.backgroundColor='#d35400'" 
                                onmouseout="this.style.backgroundColor='#f39c12'"
                                onclick="viewOrderDetails(${order.id})">
                            View
                        </button>
                    </td>
            </tr>
        `;

        orderTableBody.innerHTML += row;
    });

    console.log("🎯 UI Rendered successfully with " + orders.length + " rows!");
}

let addresses = [];

async function loadAddress() {
    // Notiflix.Loading.pulse("Wait...", {
    //     clickToClose: false,
    //     svgColor: '#0284c7'
    // });

    try {
        const response = await fetch("api/profiles/addresses");
        if (response.ok) {
            const data = await response.json();
            // document.getElementById("addName").innerHTML = `Name: ${data.name}`;
            // document.getElementById("addEmail").innerHTML = `Email: ${data.email}`;
            console.log(data);
            renderAddresses(data.addresses);
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


function renderAddresses(addresses) {
    console.log("renderAddresses");

    const list = document.getElementById("addressList");
    list.innerHTML = "";

    addresses.forEach(addr => {
        const card = document.createElement("div");

        card.className = "col-md-6" + (addr.isPrimary ? " primary" : "");

        card.innerHTML = `
            <div class="account-address">
                <h4 class="account-title">
                    ${addr.isPrimary ? "Primary Address" : "Billing Address"}
                </h4>

                <h6 class="name">
                    ${addr.name}
                </h6>

                <p id="address">
                    ${addr.lineOne}<br/>
                     ${addr.lineTwo}, ${addr.postalCode}}
                </p>

                <p>Mobile: ${addr.mobile}</p>

                <a class="btn btn-primary btn-hover-dark" href="#" onclick="editAddress(${addr.id})">
                    <i class="fa fa-edit"></i> Edit Address
                </a>
            </div>
        `;

        list.appendChild(card);
    });
}


// function renderAddresses(addresses) {
//     console.log("renderAddress")
//     const list = document.getElementById("addressList");
//     list.innerHTML = "";
//     addresses.forEach(addr => {
//         const card = document.createElement("div");
// //         card.className = "card col-12 col-md-6 col-lg-3 mx-2 my-2 address-card " + (addr.isPrimary ? "primary" : "");
// //         card.innerHTML = `
// // <div class="card-body">
// // <h6 class="card-title mb-1 text-wrap" style="word-wrap: break-word; white-space: normal;">${addr.lineOne} ${addr.lineTwo}</h6>
// // <p class="mb-1 text-muted text-wrap" style="word-wrap: break-word; white-space: normal;">${addr.cityName}</p>
// // <p class="mb-2"><strong>📞 ${addr.mobile}</strong></p>
// // <div class="d-flex gap-2">
// // <button class="btn btn-primary" ${addr.primary ? "disabled" : ""} onclick="makePrimary(${addr.id})">Make Primary</button>
// // <button class="btn btn-outline-danger" onclick="deleteAddress(${addr.id})">Delete</button>
// // </div>
// // </div>
// // `;
//
//         card.className = "col-md-6" + (addr.isPrimary ? "primary" : "");
//         card.innerHTML = `
//             <div class="account-address">
//                 <h4 class="account-title">
//                      Billing address
//                 </h4>
//                 <h6 class="name">
//                      Alex Tuntuni
//                 </h6>
//                <p id="address">
//                     1355 Market St, Suite 900
//                <br />
//                     San Francisco, CA 94103
//                </p>
//                <p >Mobile: (123) 456-7890</p>
//                    <a class="btn btn-primary btn-hover-dark" href="#"><i class="fa fa-edit"></i>
//                                                 Edit Address</a>
//             </div>
// `;
//
//         list.appendChild(card);
//     });
// }

function makePrimary(id) {

}

function deleteAddress(id) {
    if (!confirm("Are you sure you want to delete this address?")) return;

}

// async function saveChanges() {
//
//     Notiflix.Loading.pulse("Wait...", {
//         clickToClose: false,
//         svgColor: '#0284c7'
//     });
//
//     let firstName = document.getElementById("firstName");
//     let lastName = document.getElementById("lastName");
//     let lineOne = document.getElementById("lineOne");
//     let lineTwo = document.getElementById("lineTwo");
//     let postalCode = document.getElementById("postalCode");
//     let citySelect = document.getElementById("citySelect");
//     let mobile = document.getElementById("mobile");
//     let currentPassword = document.getElementById("currentPassword");
//     let newPassword = document.getElementById("newPassword");
//     let confirmPassword = document.getElementById("confirmPassword");
//
//     const userObj = {
//         firstName: firstName.value,
//         lastName: lastName.value,
//         lineOne: lineOne.value,
//         lineTwo: lineTwo.value,
//         postalCode: postalCode.value,
//         cityId: citySelect.value,
//         mobile: mobile.value,
//         password: currentPassword.value,
//         newPassword: newPassword.value,
//         confirmPassword: confirmPassword.value,
//     };
//
//     try {
//         const response = await fetch("api/profiles/update-profile", {
//             method: "PUT",
//             headers: {
//                 "Content-Type": "application/json"
//             },
//             body: JSON.stringify(userObj)
//         });
//         if (response.ok) {
//             const data = await response.json();
//             if (data.status) {
//                 Notiflix.Report.success(
//                     'SmartTrade',
//                     data.message,
//                     'Okay'
//                 );
//                 await loadUserData();
//             } else {
//                 Notiflix.Notify.failure(data.message, {
//                     position: 'center-top'
//                 });
//             }
//         } else {
//             Notiflix.Notify.failure("Profile update failed!", {
//                 position: 'center-top'
//             });
//         }
//     } catch (e) {
//         Notiflix.Notify.failure(e.message, {
//             position: 'center-top'
//         });
//     } finally {
//         Notiflix.Loading.remove(1000);
//     }
//
// }
console.log("my-account.js")
async function loadUserData() {
    try {
        const response = await fetch("api/profiles/user-profile");
        if (response.ok) {
            if (response.redirected) {
                window.location.href = response.url;
                return;
            }

            console.log("loadUserData func done!")
            const data = await response.json();

            // document.getElementById("username").innerHTML = `Hello, ${data.user.firstName} ${data.user.lastName}`;

            // let replacedText = String(data.user.sinceAt).replace("-", " ");
            // let since = replacedText.split(" ");
            // document.getElementById("since").innerHTML = `Smart Trade Member Since ${since[1]} ${since[0]}`;
            const fname = document.getElementById("firstName").value = data.user.firstName;
            const lname = document.getElementById("lastName").value = data.user.lastName;
            document.getElementById("displayName").value = fname + lname;

            // document.getElementById("lineOne").value = data.user.lineOne ? data.user.lineOne : "";
            // document.getElementById("lineTwo").value = data.user.lineTwo ? data.user.lineTwo : "";
            // document.getElementById("postalCode").value = data.user.postalCode ? data.user.postalCode : "";
            // document.getElementById("citySelect").value = data.user.city ? data.user.cityId : 0;
            // document.getElementById("mobile").value = data.user.mobile;
            const cpass = document.getElementById("currentPassword").value = data.user.password;

            // console.log(fname,lname,cpass);
        } else {
            console.log(response);
            Notiflix.Notify.failure("Profile data loading failed!", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

async function getCities() {
    try {
        const response = await fetch("api/data/cities");
        if (response.ok) {
            const data = await response.json();
            const citySelect = document.getElementById("citySelect");
            data.cities.forEach((city) => {
                const option = document.createElement("option");
                option.value = city.id;
                option.innerHTML = city.name;
                citySelect.appendChild(option); // add component as a last child
            })
        } else {
            Notiflix.Notify.failure("City loading failed!", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    }
}

async function signOut() {
    Notiflix.Loading.pulse("Wait...", {
        clickToClose: false,
        svgColor: '#0284c7'
    });

    try {
        const response = await fetch("api/users/logout", {
            method: "GET",
            credentials: "include"
        });
        if (response.ok) {
            Notiflix.Report.success(
                'SmartTrade',
                "Logout successful",
                'Okay', // button title
                () => {
                    window.location = "sign-in.html"
                },
            );
        } else {
            Notiflix.Notify.failure("Something went wrong. Log Out process failed!", {
                position: 'center-top'
            });
        }
    } catch (e) {
        Notiflix.Notify.failure(e.message, {
            position: 'center-top'
        });
    } finally {
        Notiflix.Loading.remove(1000);
    }
}

