import { useState, useEffect } from "react"
import { useParams } from "react-router"
import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"

function SalesReport() {
    const { token } = useParams()

    const [rows, setRows] = useState([])
    const [loading, setLoading] = useState(true)
    const [listErr, setListErr] = useState("")

    useEffect(() => {
        const headers = { 'Authorization': 'Bearer ' + (localStorage.getItem('token') || token) }
        const loadReport = async () => {
            try {
                const resp = await axios.get(API_SERVER_URL_DEV + '/api/order/seller/' + localStorage.getItem('id') + '/sales-report',
                    { headers: headers })
                setRows(resp.data)
                setListErr("")
            }
            catch (err) {
                setListErr(err?.response?.data?.message || "Unable to load sales report...")
            }
            finally {
                setLoading(false)
            }
        }
        loadReport()
    }, [token])

    const totalUnits = rows.reduce((sum, row) => sum + Number(row.quantitySold ?? 0), 0)
    const totalRevenue = rows.reduce((sum, row) => sum + Number(row.totalRevenue ?? 0), 0)

    return (
        <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>
            <div className="fk-card">
                <div className="fk-card-header">Sales Report</div>

                {loading && (
                    <div className="fk-empty">
                        <i className="bi bi-hourglass-split"></i>
                        Loading sales report...
                    </div>
                )}

                {!loading && listErr && (
                    <div className="p-3"><p className="fk-error">{listErr}</p></div>
                )}

                {!loading && !listErr && rows.length === 0 && (
                    <div className="fk-empty">
                        <i className="bi bi-graph-up"></i>
                        No sales yet
                    </div>
                )}

                {!loading && !listErr && rows.length > 0 && (
                    <table className="fk-table">
                        <thead>
                            <tr>
                                <th>PRODUCT</th>
                                <th>UNITS SOLD</th>
                                <th>REVENUE</th>
                            </tr>
                        </thead>
                        <tbody>
                            {rows.map((row, index) => (
                                <tr key={index}>
                                    <td className="fw-semibold">{row.productName}</td>
                                    <td>{row.quantitySold}</td>
                                    <td>Rs. {Number(row.totalRevenue ?? 0).toLocaleString('en-IN')}</td>
                                </tr>
                            ))}

                            {/* <!-- Grand total row --> */}
                            <tr style={{ background: '#f1f3f6' }}>
                                <td className="fw-bold">TOTAL</td>
                                <td className="fw-bold">{totalUnits}</td>
                                <td className="fw-bold">Rs. {totalRevenue.toLocaleString('en-IN')}</td>
                            </tr>
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    )
}

export default SalesReport
