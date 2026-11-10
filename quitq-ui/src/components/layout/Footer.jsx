function Footer() {
    const columns = [
        {
            title: "Admin",
            items: [
                "Analytics dashboard",
                "Orders by status",
                "User management",
                "Seller management",
                "Category management",
            ],
        },
        {
            title: "Customer",
            items: [
                "Sign up & login",
                "Search & filters",
                "Cart management",
                "Checkout & payment",
                "Order history",
                "Order tracking",
                "Password recovery",
            ],
        },
        {
            title: "Seller",
            items: [
                "Seller sign-up",
                "Product listings",
                "Edit & out of stock",
                "Order management",
                "Order status updates",
                "Sales report",
            ],
        },
    ]

    return (
        <footer className="fk-footer">

            {/* <!-- Blue panel: logo + role feature columns --> */}
            <div className="fk-footer-panel">
                <div className="fk-footer-stripes"></div>
                <div className="fk-footer-content">

                    {/* <!-- Left: logo only --> */}
                    <div className="fk-footer-brand">
                        <img src="/logo-transparent.png" alt="QuitQ" className="fk-footer-logo" />
                    </div>

                    {/* <!-- Right: what we built, per role --> */}
                    <div className="fk-footer-cols">
                        {columns.map((column) => (
                            <div key={column.title} className="fk-footer-col">
                                <h3 className="fk-footer-col-title">{column.title}</h3>
                                <ul className="fk-footer-list">
                                    {column.items.map((item) => (
                                        <li key={item} className="fk-footer-item">{item}</li>
                                    ))}
                                </ul>
                            </div>
                        ))}
                    </div>

                </div>
            </div>
        </footer>
    )
}

export default Footer
