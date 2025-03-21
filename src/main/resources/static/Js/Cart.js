document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ JavaScript đã tải thành công!");

    // ✅ Lắng nghe sự kiện click trên nút tăng/giảm số lượng
    document.querySelectorAll(".btn-quantity").forEach(button => {
        button.addEventListener("click", function () {
            console.log("👉 Nút được nhấn:", this);
            let sanPhamId = this.getAttribute("data-id");
            let input = document.querySelector(`.quantity-input[data-id="${sanPhamId}"]`);

            if (!input) {
                console.error("❌ Không tìm thấy input số lượng!");
                return;
            }

            let currentQuantity = parseInt(input.value);
            let newQuantity = this.classList.contains("increase") ? currentQuantity + 1 : Math.max(1, currentQuantity - 1);

            // ✅ Cập nhật giao diện ngay lập tức
            input.value = newQuantity;

            // ✅ Gửi request cập nhật số lượng lên server
            updateCartQuantity(sanPhamId, newQuantity);
        });
    });

    // ✅ Lắng nghe sự kiện thay đổi trực tiếp trên input số lượng
    document.querySelectorAll(".quantity-input").forEach(input => {
        input.addEventListener("change", function () {
            let sanPhamId = this.getAttribute("data-id");
            let newQuantity = Math.max(1, parseInt(this.value) || 1);

            // ✅ Đảm bảo UI không bị lỗi khi nhập số không hợp lệ
            this.value = newQuantity;

            // ✅ Gửi request cập nhật số lượng lên server
            updateCartQuantity(sanPhamId, newQuantity);
        });
    });

    // ✅ Đảm bảo cập nhật lại số loại sản phẩm khi load trang
    updateCartCount();
});

// ✅ Hàm gửi AJAX để cập nhật số lượng giỏ hàng và tổng tiền đơn hàng
function updateCartQuantity(sanPhamId, newQuantity) {
    if (!sanPhamId || isNaN(sanPhamId)) {
        console.error("❌ Lỗi: sanPhamId không hợp lệ!", sanPhamId);
        return;
    }

    fetch(`/cart/update`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `sanPhamId=${sanPhamId}&quantity=${newQuantity}`
    })
        .then(response => response.json())
        .then(data => {
            console.log("✅ Cập nhật thành công!", data);

            // ✅ Cập nhật tổng tiền sản phẩm
            let productPriceElement = document.querySelector(`.product-price[data-id="${sanPhamId}"]`);
            let unitPrice = parseFloat(productPriceElement.getAttribute("data-unit-price"));
            productPriceElement.textContent = (unitPrice * newQuantity).toLocaleString() + "₫";

            // ✅ Cập nhật tổng tiền đơn hàng
            document.getElementById("total-price").textContent = data.totalAmount.toLocaleString() + "₫";

            // ✅ Ẩn nút tăng nếu số lượng đạt tối đa
            let increaseButton = document.querySelector(`.btn-quantity.increase[data-id="${sanPhamId}"]`);
            if (increaseButton) {
                if (newQuantity >= data.soLuongTonKho) {
                    increaseButton.setAttribute("disabled", "true");
                } else {
                    increaseButton.removeAttribute("disabled");
                }
            }
        })
        .catch(error => console.error("❌ Lỗi khi cập nhật:", error));
}

// ✅ Hàm cập nhật số loại sản phẩm trên biểu tượng giỏ hàng
function updateCartCount() {
    fetch('/cart/count')
        .then(response => response.json())
        .then(data => {
            console.log("📌 API `/cart/count` trả về số loại sản phẩm:", data);
            let cartBadge = document.getElementById('cart-badge');
            if (cartBadge) {
                cartBadge.textContent = data; // ✅ Chỉ hiển thị số loại sản phẩm
            }
        })
        .catch(error => console.error("❌ Lỗi khi cập nhật số loại sản phẩm:", error));
}