import { useState, useEffect } from "react"
import { useParams } from "react-router"
import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"

function MyProducts() {
    const { token } = useParams()
    const sellerId = localStorage.getItem('id')
    const bearer = localStorage.getItem('token') || token
    const authHeaders = { headers: { 'Authorization': 'Bearer ' + bearer } }

    // List state
    const [products, setProducts] = useState([])
    const [categories, setCategories] = useState([])
    const [loading, setLoading] = useState(true)
    const [listErr, setListErr] = useState("")
    const [catErr, setCatErr] = useState("")

    // Add / edit form state (one form is reused for both)
    const [showForm, setShowForm] = useState(false)
    const [editingId, setEditingId] = useState(null)
    const [saving, setSaving] = useState(false)
    const [formErr, setFormErr] = useState("")
    const [actionErr, setActionErr] = useState("")
    const [name, setName] = useState("")
    const [brand, setBrand] = useState("")
    const [description, setDescription] = useState("")
    const [price, setPrice] = useState("")
    const [stockQty, setStockQty] = useState("")
    const [imageUrl, setImageUrl] = useState("")
    const [categoryId, setCategoryId] = useState("")   // holds a number once selected

    // Bumped after every mutation so the list is refetched
    const [refreshKey, setRefreshKey] = useState(0)

    useEffect(() => {
        const headers = { 'Authorization': 'Bearer ' + localStorage.getItem('token') }

        // Load only the products that belong to the logged in seller
        const loadProducts = async () => {
            try {
                const resp = await axios.get(API_SERVER_URL_DEV + '/api/product/seller/' + localStorage.getItem('id'),
                    { headers: headers })
                setProducts(resp.data)
                setListErr("")
            }
            catch (err) {
                setListErr(err?.response?.data?.message || "Unable to load products...")
            }
            finally {
                setLoading(false)
            }
        }

        // Categories feed the dropdown of the product form
        const loadCategories = async () => {
            try {
                const resp = await axios.get(API_SERVER_URL_DEV + '/api/category/all', { headers: headers })
                setCategories(resp.data)
            }
            catch (err) {
                setCatErr(err?.response?.data?.message || "Unable to load categories...")
            }
        }

        loadProducts()
        loadCategories()
    }, [refreshKey])

    const resetForm = () => {
        setName("")
        setBrand("")
        setDescription("")
        setPrice("")
        setStockQty("")
        setImageUrl("")
        setCategoryId("")
        setEditingId(null)
    }

    const openAddForm = () => {
        resetForm()
        setFormErr("")
        setShowForm(true)
    }

    // Pre-fill the same form with the product being edited
    const openEditForm = (product) => {
        resetForm()
        setName(product.name || "")
        setBrand(product.brand || "")
        setDescription(product.description || "")
        setPrice(product.price === null || product.price === undefined ? "" : product.price)
        setStockQty(product.stockQty === null || product.stockQty === undefined ? "" : product.stockQty)
        setImageUrl(product.imageUrl || "")
        setCategoryId(product.categoryId === null || product.categoryId === undefined ? "" : product.categoryId)
        setEditingId(product.id)
        setFormErr("")
        setShowForm(true)
    }

    const closeForm = () => {
        setShowForm(false)
        resetForm()
        setFormErr("")
    }

    // POST for add / PUT for edit
    const handleSubmit = async (e) => {
        e.preventDefault()
        setFormErr("")
        setSaving(true)
        try {
            const dto = {
                name: name.trim(),
                brand: brand.trim(),
                description: description.trim(),
                price: Number(price),
                stockQty: Number(stockQty),
                imageUrl: imageUrl.trim(),
                categoryId: categoryId === "" ? null : categoryId
            }
            if (editingId) {
                await axios.put(API_SERVER_URL_DEV + '/api/product/' + editingId, dto, authHeaders)
            }
            else {
                await axios.post(API_SERVER_URL_DEV + '/api/product/add/' + sellerId, dto, authHeaders)
            }
            // Refresh the list, then collapse and clear the form
            setRefreshKey((key) => key + 1)
            setShowForm(false)
            resetForm()
        }
        catch (err) {
            setFormErr(err?.response?.data?.message || "Unable to save product...")
        }
        finally {
            setSaving(false)
        }
    }

    // Backend updateStatus() only ever sets OUT_OF_STOCK (one way, not a toggle),
    // so the button only appears while the product is IN_STOCK. Editing the
    // product with stockQty > 0 sets the status back to IN_STOCK on the server.
    const markOutOfStock = async (product) => {
        if (!window.confirm('Mark "' + product.name + '" out of stock?')) return
        setActionErr("")
        try {
            await axios.put(API_SERVER_URL_DEV + '/api/product/' + product.id + '/out-of-stock', {}, authHeaders)
            setRefreshKey((key) => key + 1)
        }
        catch (err) {
            setActionErr(err?.response?.data?.message || "Unable to update status...")
        }
    }

    const deleteProduct = async (product) => {
        if (!window.confirm('Delete "' + product.name + '"? This cannot be undone.')) return
        setActionErr("")
        try {
            await axios.delete(API_SERVER_URL_DEV + '/api/product/' + product.id, authHeaders)
            setRefreshKey((key) => key + 1)
        }
        catch (err) {
            setActionErr(err?.response?.data?.message || "Unable to delete product...")
        }
    }

    return (
        <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>

            {/* <!-- Header row --> */}
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h5 className="fw-semibold mb-0">My Products ({products.length})</h5>
                <button type="button" className="fk-btn-primary" style={{ width: 'auto' }}
                    onClick={() => (showForm && editingId === null ? closeForm() : openAddForm())}>
                    ADD PRODUCT
                </button>
            </div>

            {/* <!-- Add / edit product form --> */}
            {showForm && (
                <div className="fk-card p-4 mb-3">
                    <h5 className="fk-section-title">{editingId ? 'EDIT PRODUCT' : 'ADD PRODUCT'}</h5>
                    <form onSubmit={handleSubmit}>
                        <div className="row g-3">

                            {/* <!-- Left column --> */}
                            <div className="col-md-6">
                                <div className="mb-3">
                                    <label className="fk-label" htmlFor="prod-name">Name*</label>
                                    <input id="prod-name" type="text" className="fk-input" required
                                        placeholder="Product name"
                                        value={name} onChange={(e) => setName(e.target.value)} />
                                </div>
                                <div className="mb-3">
                                    <label className="fk-label" htmlFor="prod-brand">Brand</label>
                                    <input id="prod-brand" type="text" className="fk-input"
                                        placeholder="Brand name"
                                        value={brand} onChange={(e) => setBrand(e.target.value)} />
                                </div>
                                <div className="mb-3">
                                    <label className="fk-label" htmlFor="prod-price">Price*</label>
                                    <input id="prod-price" type="number" className="fk-input" step="0.01" min="0" required
                                        placeholder="0.00"
                                        value={price} onChange={(e) => setPrice(e.target.value)} />
                                </div>
                                <div>
                                    <label className="fk-label" htmlFor="prod-stock">Stock Qty*</label>
                                    <input id="prod-stock" type="number" className="fk-input" min="0" required
                                        placeholder="0"
                                        value={stockQty} onChange={(e) => setStockQty(e.target.value)} />
                                </div>
                            </div>

                            {/* <!-- Right column --> */}
                            <div className="col-md-6">
                                <div className="mb-3">
                                    <label className="fk-label" htmlFor="prod-image">Image URL</label>
                                    <input id="prod-image" type="url" className="fk-input"
                                        placeholder="https://..."
                                        value={imageUrl} onChange={(e) => setImageUrl(e.target.value)} />
                                </div>
                                <div>
                                    <label className="fk-label" htmlFor="prod-category">Category*</label>
                                    <select id="prod-category" className="fk-input" required
                                        value={categoryId}
                                        onChange={(e) => setCategoryId(e.target.value === "" ? "" : Number(e.target.value))}>
                                        <option value="">Select category</option>
                                        {categories.map((cat) => (
                                            <option key={cat.id} value={cat.id}>{cat.name}</option>
                                        ))}
                                    </select>
                                    {catErr && <p className="fk-error mt-2">{catErr}</p>}
                                </div>
                            </div>

                            {/* <!-- Description spans both columns --> */}
                            <div className="col-12">
                                <label className="fk-label" htmlFor="prod-desc">Description</label>
                                <textarea id="prod-desc" className="fk-input" rows="3"
                                    placeholder="Short product description"
                                    value={description} onChange={(e) => setDescription(e.target.value)}></textarea>
                            </div>
                        </div>

                        {formErr && <p className="fk-error mt-3">{formErr}</p>}

                        <div className="d-flex gap-2 mt-3">
                            <button type="submit" className="fk-btn-primary" style={{ width: 'auto' }}
                                disabled={saving}>
                                {editingId ? 'UPDATE PRODUCT' : 'SUBMIT'}
                            </button>
                            <button type="button" className="fk-btn-outline" style={{ width: 'auto' }}
                                onClick={closeForm}>
                                CANCEL
                            </button>
                        </div>
                    </form>
                </div>
            )}

            {/* <!-- Product list --> */}
            <div className="fk-card">

                {actionErr && <div className="px-3 pt-3"><p className="fk-error">{actionErr}</p></div>}

                {loading && (
                    <div className="fk-empty">
                        <i className="bi bi-hourglass-split"></i>
                        Loading products...
                    </div>
                )}

                {!loading && listErr && (
                    <div className="p-3"><p className="fk-error">{listErr}</p></div>
                )}

                {!loading && !listErr && products.length === 0 && (
                    <div className="fk-empty">
                        <i className="bi bi-box-seam"></i>
                        No products yet &mdash; add your first product
                    </div>
                )}

                {!loading && !listErr && products.length > 0 && (
                    <div className="table-responsive">
                        <table className="fk-table">
                            <thead>
                                <tr>
                                    <th>IMAGE</th>
                                    <th>PRODUCT</th>
                                    <th>BRAND</th>
                                    <th>PRICE</th>
                                    <th>STOCK</th>
                                    <th>STATUS</th>
                                    <th>ACTIONS</th>
                                </tr>
                            </thead>
                            <tbody>
                                {products.map((product) => (
                                    <tr key={product.id}>
                                        <td>
                                            {product.imageUrl ? (
                                                <img src={product.imageUrl} alt={product.name}
                                                    style={{ width: 50, height: 50, objectFit: 'contain' }} />
                                            ) : (
                                                <div className="d-flex align-items-center justify-content-center fk-muted"
                                                    style={{ width: 50, height: 50, background: '#f1f3f6' }}>
                                                    <i className="bi bi-box-seam"></i>
                                                </div>
                                            )}
                                        </td>
                                        <td>
                                            <div className="fw-semibold">{product.name}</div>
                                            <small className="fk-muted d-block text-truncate" style={{ maxWidth: 260 }}>
                                                {product.description || '-'}
                                            </small>
                                        </td>
                                        <td>{product.brand || '-'}</td>
                                        <td><span className="fk-price">Rs. {Number(product.price ?? 0).toLocaleString('en-IN')}</span></td>
                                        <td>{product.stockQty}</td>
                                        <td>
                                            {product.status === 'IN_STOCK' ? (
                                                <span className="fk-badge fk-badge-green">Active</span>
                                            ) : (
                                                <span className="fk-badge fk-badge-red">Out of stock</span>
                                            )}
                                        </td>
                                        <td>
                                            <div className="d-flex gap-2">
                                                <button type="button" className="btn btn-sm btn-outline-primary"
                                                    onClick={() => openEditForm(product)}>
                                                    EDIT
                                                </button>
                                                {product.status === 'IN_STOCK' && (
                                                    <button type="button" className="btn btn-sm btn-outline-warning"
                                                        onClick={() => markOutOfStock(product)}>
                                                        MARK OUT OF STOCK
                                                    </button>
                                                )}
                                                <button type="button" className="btn btn-sm btn-outline-danger"
                                                    onClick={() => deleteProduct(product)}>
                                                    DELETE
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    )
}

export default MyProducts
