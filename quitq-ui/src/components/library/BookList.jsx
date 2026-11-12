import { useEffect, useState } from "react"
import axios from "axios"
import { API_SERVER_URL_DEV } from "../../../config"

function BookList() {
    const [books, setBooks] = useState([])
    const [yearInput, setYearInput] = useState("")
    const [minYear, setMinYear] = useState("")
    const [loading, setLoading] = useState(false)
    const [errMsg, setErrMsg] = useState("")

    useEffect(() => {
        let active = true

        const loadBooks = async () => {
            setLoading(true)
            setErrMsg("")

            try {
                let response

                if (minYear.trim() === "") {
                    response = await axios.get(`${API_SERVER_URL_DEV}/api/books`)
                } else {
                    response = await axios.get(`${API_SERVER_URL_DEV}/api/books/search`, {
                        params: { minYear: minYear }
                    })
                }

                if (active) {
                    setBooks(response.data)
                }
            } catch {
                if (active) {
                    setErrMsg("Unable to load books. Please check whether the backend is running.")
                    setBooks([])
                }
            } finally {
                if (active) {
                    setLoading(false)
                }
            }
        }

        loadBooks()

        return () => {
            active = false
        }
    }, [minYear])

    const applyYear = () => setMinYear(yearInput.trim())

    const clearYear = () => {
        setYearInput("")
        setMinYear("")
    }

    return (
        <div className="container-fluid px-4 py-4" style={{ maxWidth: 1280 }}>

            {/* <!-- Page heading + result count --> */}
            <div className="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                <h5 className="mb-0 fw-semibold fk-section-title">Library Books</h5>
                {!loading && !errMsg && (
                    <span className="fk-muted">{books.length} book{books.length !== 1 ? "s" : ""} available</span>
                )}
            </div>

            {/* <!-- Minimum year filter: draft inputs committed on Apply (same pattern as the catalogue filters) --> */}
            <div className="fk-card p-3 mb-3">
                <div className="row g-2 align-items-end">
                    <div className="col-sm-6 col-md-4">
                        <label className="fk-label" htmlFor="minYear">MINIMUM PUBLISHED YEAR</label>
                        <input id="minYear" type="number" min="0" className="fk-input"
                            placeholder="e.g. 1950"
                            value={yearInput}
                            onChange={(event) => setYearInput(event.target.value)} />
                    </div>
                    <div className="col-sm-6 col-md-3">
                        <button type="button" className="fk-btn-blue" onClick={() => applyYear()}>Apply</button>
                    </div>
                    <div className="col-sm-6 col-md-3">
                        <button type="button" className="fk-btn-outline" onClick={() => clearYear()}>Clear</button>
                    </div>
                </div>
            </div>

            {/* <!-- Books table --> */}
            <div className="fk-card">
                <div className="fk-card-header">
                    {minYear ? `Books published in ${minYear} or later` : "All books"}
                </div>

                {loading && (
                    <div className="p-3" aria-hidden="true">
                        {[0, 1, 2].map((row) => (
                            <div key={row} className="d-flex gap-3 mb-3">
                                <div className="fk-skeleton" style={{ width: 40, height: 14 }}></div>
                                <div className="fk-skeleton" style={{ flex: 1, height: 14 }}></div>
                                <div className="fk-skeleton" style={{ width: 120, height: 14 }}></div>
                                <div className="fk-skeleton" style={{ width: 70, height: 14 }}></div>
                            </div>
                        ))}
                    </div>
                )}

                {!loading && errMsg && (
                    <div className="p-4">
                        <p className="fk-error">{errMsg}</p>
                    </div>
                )}

                {!loading && !errMsg && (
                    <div className="table-responsive">
                        <table className="fk-table">
                            <thead>
                                <tr>
                                    <th style={{ width: 60 }}>ID</th>
                                    <th>Title</th>
                                    <th>Genre</th>
                                    <th>Published Year</th>
                                    <th>Price</th>
                                    <th>Author</th>
                                </tr>
                            </thead>
                            <tbody>
                                {books.length > 0 ? books.map((book) => (
                                    <tr key={book.id}>
                                        <td className="fk-muted">{book.id}</td>
                                        <td className="fw-semibold">{book.title}</td>
                                        <td>{book.genre}</td>
                                        <td>{book.publishedYear}</td>
                                        <td className="fw-semibold">Rs. {Number(book.price).toLocaleString('en-IN')}</td>
                                        <td>{book.authorName}</td>
                                    </tr>
                                )) : (
                                    <tr>
                                        <td colSpan="6">
                                            <div className="fk-empty">
                                                <i className="bi bi-journal-bookmark"></i>
                                                No books found
                                            </div>
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    )
}

export default BookList
