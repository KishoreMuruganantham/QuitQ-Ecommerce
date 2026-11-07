import { useEffect, useState } from "react"
import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"

function ManageCategories() {
    const [categories, setCategories] = useState([])
    const [loading, setLoading] = useState(true)
    const [listErr, setListErr] = useState("")

    // Add form state
    const [name, setName] = useState("")
    const [description, setDescription] = useState("")
    const [seq, setSeq] = useState("")
    const [addErr, setAddErr] = useState("")

    // Inline edit state (only one row at a time)
    const [editingId, setEditingId] = useState(null)
    const [editName, setEditName] = useState("")
    const [editDesc, setEditDesc] = useState("")
    const [editSeq, setEditSeq] = useState("")
    const [editErr, setEditErr] = useState("")
    const [delErr, setDelErr] = useState("")

    // Bumped after every add / edit / delete so the list is refetched
    const [refreshKey, setRefreshKey] = useState(0)

    const authHeaders = { 'Authorization': 'Bearer ' + localStorage.getItem('token') }

    useEffect(() => {
        const headers = { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
        const loadCategories = async () => {
            try {
                const resp = await axios.get(API_SERVER_URL_DEV + '/api/category/all', { headers: headers })
                setListErr("")
                setCategories(resp.data)
            }
            catch (err) {
                setListErr(err?.response?.data?.message || "Unable to load categories...")
            }
            finally {
                setLoading(false)
            }
        }
        loadCategories()
    }, [refreshKey])

    // Convert the seq input (optional) into an Integer or null
    const toSeq = (value) => (value === "" || value === null || value === undefined ? null : Number(value))

    const addCategory = async (e) => {
        e.preventDefault()
        setAddErr("")
        try {
            const dto = { name: name, description: description, seq: toSeq(seq) }
            await axios.post(API_SERVER_URL_DEV + '/api/category/add', dto, { headers: authHeaders })
            // Clear the form and reload the list
            setName("")
            setDescription("")
            setSeq("")
            setRefreshKey((key) => key + 1)
        }
        catch (err) {
            setAddErr(err?.response?.data?.message || "Unable to add category...")
        }
    }

    const startEdit = (cat) => {
        setEditingId(cat.id)
        setEditName(cat.name || "")
        setEditDesc(cat.description || "")
        setEditSeq(cat.seq === null || cat.seq === undefined ? "" : cat.seq)
        setEditErr("")
        setDelErr("")
    }

    const cancelEdit = () => {
        setEditingId(null)
        setEditErr("")
    }

    const saveEdit = async () => {
        setEditErr("")
        try {
            const dto = { name: editName, description: editDesc, seq: toSeq(editSeq) }
            await axios.put(API_SERVER_URL_DEV + '/api/category/' + editingId, dto, { headers: authHeaders })
            setEditingId(null)
            setRefreshKey((key) => key + 1)
        }
        catch (err) {
            setEditErr(err?.response?.data?.message || "Unable to update category...")
        }
    }

    const deleteCategory = async (id) => {
        if (!window.confirm("Delete this category?")) return
        setDelErr("")
        try {
            // Refetch (refreshKey) after a successful delete
            await axios.delete(API_SERVER_URL_DEV + '/api/category/' + id, { headers: authHeaders })
            setRefreshKey((key) => key + 1)
        }
        catch (err) {
            setDelErr(err?.response?.data?.message || "Unable to delete category...")
        }
    }

    return (
        <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>

            {/* <!-- Add category form --> */}
            <div className="fk-card mb-3">
                <div className="fk-card-header">ADD CATEGORY</div>
                <form className="p-3" onSubmit={addCategory}>
                    <div className="row g-3">
                        <div className="col-md-4">
                            <label className="fk-label" htmlFor="cat-name">Name*</label>
                            <input id="cat-name" type="text" className="fk-input" required
                                placeholder="Category name"
                                value={name} onChange={(e) => setName(e.target.value)} />
                        </div>
                        <div className="col-md-4">
                            <label className="fk-label" htmlFor="cat-desc">Description</label>
                            <input id="cat-desc" type="text" className="fk-input"
                                placeholder="Short description"
                                value={description} onChange={(e) => setDescription(e.target.value)} />
                        </div>
                        <div className="col-md-2">
                            <label className="fk-label" htmlFor="cat-seq">Seq</label>
                            <input id="cat-seq" type="number" className="fk-input"
                                placeholder="Display order"
                                value={seq} onChange={(e) => setSeq(e.target.value)} />
                        </div>
                        <div className="col-md-2 d-flex align-items-end">
                            <button type="submit" className="fk-btn-primary" style={{ width: 'auto' }}>
                                ADD
                            </button>
                        </div>
                    </div>
                    {addErr && <p className="fk-error mt-3">{addErr}</p>}
                </form>
            </div>

            {/* <!-- Category list --> */}
            <div className="fk-card">
                <div className="fk-card-header">Categories ({categories.length})</div>

                {/* <!-- Delete errors (e.g. category still in use) --> */}
                {delErr && <div className="px-3 pt-3"><p className="fk-error">{delErr}</p></div>}

                {loading && <div className="fk-empty">Loading...</div>}

                {!loading && listErr && (
                    <div className="p-3"><p className="fk-error">{listErr}</p></div>
                )}

                {!loading && !listErr && categories.length === 0 && (
                    <div className="fk-empty">
                        <i className="bi bi-tags"></i>
                        No categories yet
                    </div>
                )}

                {!loading && !listErr && categories.length > 0 && (
                    <table className="fk-table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Seq</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {
                                categories.map((cat, index) => (
                                    editingId === cat.id ? (
                                        /* <!-- Row flipped into inline edit mode --> */
                                        <tr key={cat.id}>
                                            <td className="fk-muted">{index + 1}</td>
                                            <td>
                                                <input type="text" className="fk-input"
                                                    value={editName} onChange={(e) => setEditName(e.target.value)} />
                                            </td>
                                            <td>
                                                <input type="text" className="fk-input"
                                                    value={editDesc} onChange={(e) => setEditDesc(e.target.value)} />
                                            </td>
                                            <td>
                                                <input type="number" className="fk-input"
                                                    value={editSeq} onChange={(e) => setEditSeq(e.target.value)} />
                                            </td>
                                            <td>
                                                <div className="d-flex gap-2">
                                                    <button className="fk-btn-blue" style={{ width: 'auto', padding: '6px 12px' }}
                                                        onClick={saveEdit}>
                                                        SAVE
                                                    </button>
                                                    <button className="fk-btn-outline" style={{ width: 'auto', padding: '6px 12px' }}
                                                        onClick={cancelEdit}>
                                                        CANCEL
                                                    </button>
                                                </div>
                                                {editErr && <p className="fk-error mt-2">{editErr}</p>}
                                            </td>
                                        </tr>
                                    ) : (
                                        <tr key={cat.id}>
                                            <td className="fk-muted">{index + 1}</td>
                                            <td className="fw-semibold">{cat.name}</td>
                                            <td className="fk-muted">{cat.description || '-'}</td>
                                            <td>{cat.seq ?? '-'}</td>
                                            <td>
                                                <div className="d-flex gap-2">
                                                    <button className="btn btn-sm btn-outline-primary"
                                                        onClick={() => startEdit(cat)}>
                                                        EDIT
                                                    </button>
                                                    <button className="btn btn-sm btn-outline-danger"
                                                        onClick={() => deleteCategory(cat.id)}>
                                                        DELETE
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    )
                                ))
                            }
                        </tbody>
                    </table>
                )}
            </div>

        </div>
    )
}

export default ManageCategories
