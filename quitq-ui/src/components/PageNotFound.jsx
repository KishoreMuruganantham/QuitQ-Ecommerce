import { Link } from "react-router"

function PageNotFound() {
    return (
        <div className="fk-empty d-flex flex-column align-items-center justify-content-center"
            style={{ minHeight: '100vh', backgroundColor: '#f1f3f6' }}>
            <div className="fk-card mx-auto mt-5 p-5 text-center" style={{ maxWidth: 440 }}>
                <i className="bi bi-emoji-frown"></i>
                <h2 className="fw-semibold">Page not found</h2>
                <p className="fk-muted mb-4">The page you are looking for does not exist.</p>
                <Link to="/" className="fk-btn-primary text-decoration-none text-center"
                    style={{ width: 'auto', display: 'inline-block', padding: '10px 24px' }}>
                    GO TO LOGIN
                </Link>
            </div>
        </div>
    )
}

export default PageNotFound
