import { useEffect, useState } from "react"
import { Link, useNavigate, useParams } from "react-router"
import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"

function ProductDetail() {
    const { token, id } = useParams()
    const navigate = useNavigate()

    const [product, setProduct] = useState(null)
    const [loading, setLoading] = useState(true)
    const [errMsg, setErrMsg] = useState("")
    const [addErr, setAddErr] = useState("")
    const [qty, setQty] = useState(1)
    const [added, setAdded] = useState(false)

    // Load the product once per route id
    useEffect(() => {
        const loadProduct = async () => {
            setLoading(true)
            setErrMsg("")
            setAddErr("")
            setAdded(false)
            setQty(1)
            try {
                const resp = await axios.get(`${API_SERVER_URL_DEV}/api/product/${id}`, {
                    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
                })
                setProduct(resp.data)
            } catch (err) {
                setProduct(null)
                setErrMsg(err?.response?.data?.message || "")
            } finally {
                setLoading(false)
            }
        }
        loadProduct()
    }, [id])

    const isOutOfStock = product?.status === 'OUT_OF_STOCK'
    const stockQty = Number(product?.stockQty) || 0
    // never allow adding more than 10 in one go
    const maxQty = Math.min(Math.max(stockQty, 1), 10)

    const postToCart = async () => {
        setAddErr("")
        try {
            await axios.post(
                `${API_SERVER_URL_DEV}/api/cart/add/${localStorage.getItem('id')}/${id}?quantity=${qty}`,
                null,
                { headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') } }
            )
            return true
        } catch (err) {
            setAddErr(err?.response?.data?.message || 'Unable to add this product to cart')
            return false
        }
    }

    const handleAddToCart = async () => {
        const ok = await postToCart()
        if (ok) setAdded(true)
    }

    const handleBuyNow = async () => {
        const ok = await postToCart()
        if (ok) navigate(`/customer/${token}/checkout`)
    }

    const backLink = (
        <Link to={`/customer/${token}`} className="fw-semibold text-decoration-none d-inline-block mb-3"
            style={{ color: '#2874F0' }}>
            ← Back to Home
        </Link>
    )

    if (loading) {
        return (
            <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>
                <div className="text-center py-5 fk-muted">Loading product...</div>
            </div>
        )
    }

    return (
        <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>

            {/* <!-- Product not found --> */}
            {!product && (
                <div className="fk-card fk-empty">
                    <i className="bi bi-box-seam"></i>
                    <div className="fw-semibold mb-1">Product not found</div>
                    {errMsg && <p className="fk-error mb-3">{errMsg}</p>}
                    <Link to={`/customer/${token}`} className="fw-semibold text-decoration-none"
                        style={{ color: '#2874F0' }}>← Back to Home</Link>
                </div>
            )}

            {product && (
                <>
                    {backLink}

                    {/* <!-- Product main card --> */}
                    <div className="fk-card p-4">
                        <div className="row g-4">

                            {/* <!-- Image --> */}
                            <div className="col-md-5">
                                {product.imageUrl ? (
                                    <img src={product.imageUrl} alt={product.name}
                                        style={{ width: '100%', height: 360, objectFit: 'contain' }} />
                                ) : (
                                    <div className="d-flex align-items-center justify-content-center"
                                        style={{ height: 360, backgroundColor: '#fafafa' }}>
                                        <i className="bi bi-image fs-1 text-secondary"></i>
                                    </div>
                                )}
                            </div>

                            {/* <!-- Info --> */}
                            <div className="col-md-7">
                                <h4 className="fw-semibold mb-1">{product.name}</h4>
                                <div className="fk-muted mb-2">{product.brand}</div>

                                <div className="d-flex align-items-center gap-3 flex-wrap mb-2">
                                    <span className="fk-price-lg">Rs. {Number(product.price).toLocaleString('en-IN')}</span>
                                    <span className={`fk-badge ${product.status === 'IN_STOCK' ? 'fk-badge-green' : 'fk-badge-red'}`}>
                                        {product.status === 'IN_STOCK' ? 'In stock' : 'Out of stock'}
                                    </span>
                                </div>

                                {/* <!-- Key details --> */}
                                <dl className="row mb-0 mt-3">
                                    <dt className="col-4 fk-muted small fw-normal">Seller</dt>
                                    <dd className="col-8 mb-2">{product.sellerName}</dd>

                                    <dt className="col-4 fk-muted small fw-normal">Category</dt>
                                    <dd className="col-8 mb-2">{product.categoryName}</dd>

                                    <dt className="col-4 fk-muted small fw-normal">Stock</dt>
                                    <dd className="col-8 mb-0">
                                        {product.status === 'IN_STOCK' ? (
                                            <>
                                                <span className="fw-semibold" style={{ color: '#388E3C' }}>In stock</span>
                                                {stockQty <= 10 ? (
                                                    <span className="fw-semibold ms-2" style={{ color: '#FB641B' }}>
                                                        Only {stockQty} left
                                                    </span>
                                                ) : (
                                                    <span className="fk-muted ms-2">({stockQty} available)</span>
                                                )}
                                            </>
                                        ) : (
                                            <span className="fw-semibold" style={{ color: '#d32f2f' }}>Out of stock</span>
                                        )}
                                    </dd>
                                </dl>

                                {/* <!-- Quantity selector --> */}
                                {product.status === 'IN_STOCK' && (
                                    <div className="d-flex align-items-center gap-3 mt-4">
                                        <span className="fk-label mb-0">Quantity</span>
                                        <div className="d-flex align-items-center gap-2">
                                            <button type="button" className="fk-qty-btn" disabled={qty <= 1}
                                                onClick={() => setQty((prev) => prev - 1)}>−</button>
                                            <span className="fk-qty-val">{qty}</span>
                                            <button type="button" className="fk-qty-btn" disabled={qty >= maxQty}
                                                onClick={() => setQty((prev) => prev + 1)}>+</button>
                                        </div>
                                    </div>
                                )}

                                {/* <!-- Purchase actions --> */}
                                <div className="row g-2 mt-3">
                                    <div className="col">
                                        <button type="button" className="fk-btn-primary" disabled={isOutOfStock}
                                            onClick={() => handleAddToCart()}>ADD TO CART</button>
                                    </div>
                                    <div className="col">
                                        <button type="button" className="fk-btn-blue" disabled={isOutOfStock}
                                            onClick={() => handleBuyNow()}>BUY NOW</button>
                                    </div>
                                </div>

                                {isOutOfStock && (
                                    <p className="fk-error mt-2 mb-0">This product is currently out of stock</p>
                                )}

                                {addErr && <p className="fk-error mt-2 mb-0">{addErr}</p>}

                                {/* <!-- Added to cart confirmation --> */}
                                {added && (
                                    <div className="d-flex align-items-center gap-3 mt-3 flex-wrap">
                                        <span className="fw-semibold" style={{ color: '#388E3C' }}>Added to cart ✓</span>
                                        <button type="button" className="fk-btn-outline w-auto"
                                            onClick={() => navigate(`/customer/${token}/cart`)}>GO TO CART</button>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* <!-- Description --> */}
                    <div className="fk-card p-4 mt-4">
                        <div className="fk-section-title">Description</div>
                        <p className="mb-0" style={{ whiteSpace: 'pre-wrap' }}>
                            {product.description
                                ? product.description
                                : <span className="fk-muted">No description available</span>}
                        </p>
                    </div>
                </>
            )}
        </div>
    )
}

export default ProductDetail
