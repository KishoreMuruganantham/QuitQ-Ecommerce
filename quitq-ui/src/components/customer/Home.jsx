import { useEffect, useState } from "react"
import { useNavigate, useParams, useSearchParams } from "react-router"
import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"

// Category icon by name (case-insensitive contains)
const categoryIcon = (name) => {
    const n = String(name || '').toLowerCase()
    if (n.includes('electronics')) return 'bi-tv'
    if (n.includes('mobile')) return 'bi-phone'
    if (n.includes('fashion')) return 'bi-handbag'
    if (n.includes('home')) return 'bi-house'
    return 'bi-tag'
}

function Home() {
    const { token } = useParams()
    const navigate = useNavigate()
    const [searchParams] = useSearchParams()
    const q = (searchParams.get('q') || '').trim()

    const [products, setProducts] = useState([])
    const [categories, setCategories] = useState([])
    const [loading, setLoading] = useState(true)
    const [errMsg, setErrMsg] = useState("")
    const [page, setPage] = useState(0)
    const [filters, setFilters] = useState({ brand: "", minPrice: "", maxPrice: "", categoryId: "" })
    const [showFilters, setShowFilters] = useState(false)

    // Draft values for the filter panel inputs (applied only when Apply is clicked)
    const [brandInput, setBrandInput] = useState("")
    const [minInput, setMinInput] = useState("")
    const [maxInput, setMaxInput] = useState("")

    const activeFilters = [filters.brand, filters.minPrice !== "", filters.maxPrice !== ""].filter(Boolean).length

    // Category strip loads once
    useEffect(() => {
        const loadCategories = async () => {
            try {
                const resp = await axios.get(API_SERVER_URL_DEV + '/api/category/all')
                const sorted = [...resp.data].sort((a, b) => (a.seq || 0) - (b.seq || 0))
                setCategories(sorted)
            } catch {
                // category strip is optional, catalog still works without it
            }
        }
        loadCategories()
    }, [])

    // Products reload whenever filters, page or search keyword change
    useEffect(() => {
        const loadProducts = async () => {
            setLoading(true)
            setErrMsg("")
            try {
                let url
                if (q) {
                    url = `${API_SERVER_URL_DEV}/api/product/search?keyword=${encodeURIComponent(q)}&size=12`
                } else {
                    const params = new URLSearchParams()
                    if (filters.brand) params.append('brand', filters.brand)
                    if (filters.categoryId !== "") params.append('categoryId', filters.categoryId)
                    if (filters.minPrice !== "") params.append('minPrice', filters.minPrice)
                    if (filters.maxPrice !== "") params.append('maxPrice', filters.maxPrice)
                    params.append('page', page)
                    params.append('size', 12)
                    url = `${API_SERVER_URL_DEV}/api/product/filter?${params.toString()}`
                }
                const resp = await axios.get(url, {
                    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
                })
                setProducts(resp.data)
            } catch (err) {
                setProducts([])
                setErrMsg(err?.response?.data?.message || 'Unable to load products. Please try again.')
            } finally {
                setLoading(false)
            }
        }
        loadProducts()
    }, [filters, page, q])

    const selectCategory = (categoryId) => {
        setFilters((prev) => ({ ...prev, categoryId }))
        setPage(0)
        // Choosing a category while a keyword search is active clears the search
        if (q) navigate(`/customer/${token}`)
    }

    const applyBrand = () => {
        setFilters((prev) => ({ ...prev, brand: brandInput.trim() }))
        setPage(0)
    }

    const clearBrand = () => {
        setBrandInput("")
        setFilters((prev) => ({ ...prev, brand: "" }))
        setPage(0)
    }

    const applyPrice = () => {
        setFilters((prev) => ({ ...prev, minPrice: minInput, maxPrice: maxInput }))
        setPage(0)
    }

    const clearPrice = () => {
        setMinInput("")
        setMaxInput("")
        setFilters((prev) => ({ ...prev, minPrice: "", maxPrice: "" }))
        setPage(0)
    }

    const clearSearch = () => {
        navigate(`/customer/${token}`)
    }

    const openProduct = (productId) => {
        navigate(`/customer/${token}/product/${productId}`)
    }

    return (
        <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>

            {/* <!-- Category circles + Filters button --> */}
            <div className="fk-card p-3 mb-3 d-flex align-items-center gap-3 flex-wrap">
                <div className="d-flex flex-nowrap overflow-x-auto gap-1 flex-grow-1">
                    <button type="button"
                        className={`fk-cat fk-cat-tint-5 flex-shrink-0 ${!filters.categoryId ? 'fk-cat-active' : ''}`}
                        onClick={() => selectCategory("")}>
                        <span className="fk-cat-circle"><i className="bi bi-grid"></i></span>
                        <span className="fk-cat-label">All</span>
                    </button>
                    {categories.map((c, i) => (
                        <button type="button" key={c.id}
                            className={`fk-cat fk-cat-tint-${(i % 4) + 1} flex-shrink-0 ${filters.categoryId === c.id ? 'fk-cat-active' : ''}`}
                            onClick={() => selectCategory(c.id)}>
                            <span className="fk-cat-circle"><i className={`bi ${categoryIcon(c.name)}`}></i></span>
                            <span className="fk-cat-label">{c.name}</span>
                        </button>
                    ))}
                </div>

                {/* <!-- Filters are opt-in: hidden while a keyword search is active --> */}
                {!q && (
                    <button type="button"
                        className={`fk-btn-filter ${showFilters ? 'fk-btn-filter-active' : ''}`}
                        onClick={() => setShowFilters((prev) => !prev)}>
                        <i className="bi bi-funnel"></i>Filters
                        {activeFilters > 0 && (
                            <span className="fk-badge fk-badge-orange">{activeFilters}</span>
                        )}
                    </button>
                )}
            </div>

            {/* <!-- Collapsible filter panel --> */}
            {showFilters && !q && (
                <div className="fk-card p-3 mb-3">
                    <div className="row g-4">

                        {/* <!-- Brand filter --> */}
                        <div className="col-md-6 col-lg-4">
                            <label className="fk-label" htmlFor="brandFilter">BRAND</label>
                            <input id="brandFilter" type="text" className="fk-input" placeholder="e.g. Apple"
                                value={brandInput} onChange={(e) => setBrandInput(e.target.value)} />
                            <div className="row g-2 mt-1">
                                <div className="col">
                                    <button type="button" className="fk-btn-blue" onClick={() => applyBrand()}>Apply</button>
                                </div>
                                <div className="col">
                                    <button type="button" className="fk-btn-outline" onClick={() => clearBrand()}>Clear</button>
                                </div>
                            </div>
                        </div>

                        {/* <!-- Price filter --> */}
                        <div className="col-md-6 col-lg-4">
                            <label className="fk-label" htmlFor="minPriceFilter">PRICE</label>
                            <div className="row g-2">
                                <div className="col">
                                    <input id="minPriceFilter" type="number" className="fk-input" placeholder="Min" min="0"
                                        value={minInput} onChange={(e) => setMinInput(e.target.value)} />
                                </div>
                                <div className="col">
                                    <input id="maxPriceFilter" type="number" className="fk-input" placeholder="Max" min="0"
                                        value={maxInput} onChange={(e) => setMaxInput(e.target.value)} />
                                </div>
                            </div>
                            <div className="row g-2 mt-1">
                                <div className="col">
                                    <button type="button" className="fk-btn-blue" onClick={() => applyPrice()}>Apply</button>
                                </div>
                                <div className="col">
                                    <button type="button" className="fk-btn-outline" onClick={() => clearPrice()}>Clear</button>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            )}

            {/* <!-- Results header --> */}
            <div className="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                <h5 className="mb-0 fw-semibold fk-section-title">
                    {q ? <>Results for '{q}'</> : 'Products'}
                </h5>
                {q && (
                    <button type="button" className="fk-btn-outline w-auto"
                        onClick={() => clearSearch()} title="Clear search">
                        <i className="bi bi-x-lg me-1"></i>Clear
                    </button>
                )}
            </div>

            {loading && (
                        <div className="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-3" aria-hidden="true">
                            {Array.from({ length: 6 }).map((_, i) => (
                                <div className="col" key={`skeleton-${i}`}>
                                    <div className="fk-card p-3">
                                        <div className="fk-skeleton mb-3" style={{ height: 200 }}></div>
                                        <div className="fk-skeleton mb-2" style={{ height: 12, width: '70%' }}></div>
                                        <div className="fk-skeleton" style={{ height: 14, width: '45%' }}></div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}

                    {!loading && errMsg && (
                        <p className="fk-error text-center py-4">{errMsg}</p>
                    )}

                    {!loading && !errMsg && products.length === 0 && (
                        <div className="fk-card fk-empty">
                            <i className="bi bi-cart-x"></i>
                            No products found
                        </div>
                    )}

                    {!loading && products.length > 0 && (
                        <>
                            {/* <!-- Product grid --> */}
                            <div className="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-3">
                                {products.map((p) => (
                                    <div className="col" key={p.id}>
                                        <div className="fk-product-card" onClick={() => openProduct(p.id)}>
                                            {p.imageUrl ? (
                                                <img className="fk-product-img" src={p.imageUrl} alt={p.name} />
                                            ) : (
                                                <div className="d-flex align-items-center justify-content-center"
                                                    style={{ height: 200, backgroundColor: '#fafafa' }}>
                                                    <i className="bi bi-image fs-1 text-secondary"></i>
                                                </div>
                                            )}
                                            <div className="p-3">
                                                <div className="fw-semibold text-truncate">{p.name}</div>
                                                <div className="fk-muted small mb-2">{p.brand}</div>
                                                <div className="d-flex align-items-center gap-2 flex-wrap">
                                                    <span className="fk-price">Rs. {Number(p.price).toLocaleString('en-IN')}</span>
                                                    <span className={`fk-badge ${p.status === 'IN_STOCK' ? 'fk-badge-green' : 'fk-badge-red'}`}>
                                                        {p.status === 'IN_STOCK' ? 'In stock' : 'Out of stock'}
                                                    </span>
                                                </div>
                                                <div className="text-success small mt-1">Free delivery</div>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>

                            {/* <!-- Pagination (search results are not paginated) --> */}
                            {!q && (
                                <div className="d-flex justify-content-center align-items-center gap-3 mt-4">
                                    <button type="button" className="fk-btn-outline w-auto" disabled={page === 0}
                                        style={page === 0 ? { color: '#c2c2c2', borderColor: '#e0e0e0' } : undefined}
                                        onClick={() => setPage((prev) => prev - 1)}>Prev</button>
                                    <span className="fw-semibold">Page {page + 1}</span>
                                    <button type="button" className="fk-btn-outline w-auto" disabled={products.length < 12}
                                        style={products.length < 12 ? { color: '#c2c2c2', borderColor: '#e0e0e0' } : undefined}
                                        onClick={() => setPage((prev) => prev + 1)}>Next</button>
                                </div>
                            )}
                        </>
                    )}
        </div>
    )
}

export default Home
