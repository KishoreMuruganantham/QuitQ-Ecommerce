import { useEffect, useState } from "react"
import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"

function ManageUsers() {
    // 'customers' | 'sellers'
    const [tab, setTab] = useState("customers")

    const [customers, setCustomers] = useState([])
    const [sellers, setSellers] = useState([])

    const [custLoading, setCustLoading] = useState(true)
    const [sellLoading, setSellLoading] = useState(true)

    const [custErr, setCustErr] = useState("")
    const [sellErr, setSellErr] = useState("")

    // Bumped after every delete so the lists are refetched
    const [refreshKey, setRefreshKey] = useState(0)

    useEffect(() => {
        const loadCustomers = async () => {
            try {
                const resp = await axios.get(API_SERVER_URL_DEV + '/api/customer/all?page=0&size=100', {
                    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
                })
                setCustErr("")
                setCustomers(resp.data)
            }
            catch (err) {
                setCustErr(err?.response?.data?.message || "Unable to load customers...")
            }
            finally {
                setCustLoading(false)
            }
        }

        const loadSellers = async () => {
            try {
                const resp = await axios.get(API_SERVER_URL_DEV + '/api/seller/all?page=0&size=100', {
                    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
                })
                setSellErr("")
                setSellers(resp.data)
            }
            catch (err) {
                setSellErr(err?.response?.data?.message || "Unable to load sellers...")
            }
            finally {
                setSellLoading(false)
            }
        }

        loadCustomers()
        loadSellers()
    }, [refreshKey])

    const deleteCustomer = async (id) => {
        if (!window.confirm("Delete this customer?")) return
        setCustLoading(true)
        setCustErr("")
        try {
            // Refetch (refreshKey) after a successful delete
            await axios.delete(API_SERVER_URL_DEV + '/api/customer/' + id, {
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
            })
            setRefreshKey((key) => key + 1)
        }
        catch (err) {
            setCustLoading(false)
            setCustErr(err?.response?.data?.message || "Unable to delete customer...")
        }
    }

    const deleteSeller = async (id) => {
        if (!window.confirm("Delete this seller?")) return
        setSellLoading(true)
        setSellErr("")
        try {
            // Refetch (refreshKey) after a successful delete
            await axios.delete(API_SERVER_URL_DEV + '/api/seller/' + id, {
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
            })
            setRefreshKey((key) => key + 1)
        }
        catch (err) {
            setSellLoading(false)
            setSellErr(err?.response?.data?.message || "Unable to delete seller...")
        }
    }

    // Count shown in the page heading follows the active tab
    const currentCount = tab === "customers" ? customers.length : sellers.length

    return (
        <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>

            {/* <!-- Page heading --> */}
            <h5 className="fw-semibold mb-3">Manage Users ({currentCount})</h5>

            {/* <!-- Customers / Sellers tab switch --> */}
            <div className="d-flex gap-2 mb-3">
                <button
                    className={tab === "customers" ? "fk-btn-blue" : "fk-btn-outline"}
                    style={{ width: 'auto', borderRadius: 999, padding: '8px 22px' }}
                    onClick={() => setTab("customers")}>
                    Customers
                </button>
                <button
                    className={tab === "sellers" ? "fk-btn-blue" : "fk-btn-outline"}
                    style={{ width: 'auto', borderRadius: 999, padding: '8px 22px' }}
                    onClick={() => setTab("sellers")}>
                    Sellers
                </button>
            </div>

            {/* <!-- Customers table --> */}
            {tab === "customers" && (
                <div className="fk-card">
                    {custLoading && <div className="fk-empty">Loading...</div>}
                    {/* <!-- Load / delete errors (e.g. foreign key) shown without crashing --> */}
                    {!custLoading && custErr && (
                        <div className="p-3"><p className="fk-error">{custErr}</p></div>
                    )}
                    {!custLoading && !custErr && customers.length === 0 && (
                        <div className="fk-empty">
                            <i className="bi bi-people"></i>
                            No customers yet
                        </div>
                    )}
                    {!custLoading && customers.length > 0 && !custErr && (
                        <table className="fk-table">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Contact</th>
                                    <th>Username</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    customers.map((c, index) => (
                                        <tr key={c.id}>
                                            <td className="fk-muted">{index + 1}</td>
                                            <td className="fw-semibold">{c.name}</td>
                                            <td>{c.email}</td>
                                            <td>{c.contactNumber}</td>
                                            <td>{c.username}</td>
                                            <td>
                                                <button className="btn btn-sm btn-outline-danger"
                                                    onClick={() => deleteCustomer(c.id)}>
                                                    DELETE
                                                </button>
                                            </td>
                                        </tr>
                                    ))
                                }
                            </tbody>
                        </table>
                    )}
                </div>
            )}

            {/* <!-- Sellers table --> */}
            {tab === "sellers" && (
                <div className="fk-card">
                    {sellLoading && <div className="fk-empty">Loading...</div>}
                    {/* <!-- Load / delete errors (e.g. foreign key) shown without crashing --> */}
                    {!sellLoading && sellErr && (
                        <div className="p-3"><p className="fk-error">{sellErr}</p></div>
                    )}
                    {!sellLoading && !sellErr && sellers.length === 0 && (
                        <div className="fk-empty">
                            <i className="bi bi-people"></i>
                            No sellers yet
                        </div>
                    )}
                    {!sellLoading && sellers.length > 0 && !sellErr && (
                        <table className="fk-table">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Business Name</th>
                                    <th>Contact</th>
                                    <th>City</th>
                                    <th>Email</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    sellers.map((s, index) => (
                                        <tr key={s.id}>
                                            <td className="fk-muted">{index + 1}</td>
                                            <td className="fw-semibold">{s.businessName}</td>
                                            <td>{s.contactNumber}</td>
                                            <td>{s.city}</td>
                                            <td>{s.email}</td>
                                            <td>
                                                <button className="btn btn-sm btn-outline-danger"
                                                    onClick={() => deleteSeller(s.id)}>
                                                    DELETE
                                                </button>
                                            </td>
                                        </tr>
                                    ))
                                }
                            </tbody>
                        </table>
                    )}
                </div>
            )}

        </div>
    )
}

export default ManageUsers
