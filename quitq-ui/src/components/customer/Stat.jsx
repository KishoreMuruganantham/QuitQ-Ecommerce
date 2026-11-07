import { useEffect, useState } from "react"
import { useSelector } from "react-redux"

function Stat(){
    const list = useSelector(state=>state.cartItems)
    const totalQuantity = list.reduce((sum, c)=> sum + c.quantity, 0);
    const totalAmount = list.reduce((sum, c)=> sum + c.lineTotal, 0);

    return(
        <div className="card border-0 shadow-sm" style={{ borderRadius: 2 }}>
            <div className="card-header bg-white" style={{ borderBottom: '2px solid #2874F0' }}>
                <h5 className="mb-0 fw-semibold">PRICE DETAILS</h5>
            </div>
            <div className="card-body">
                <div className="d-flex justify-content-between mb-2">
                    <span className="text-muted">Price ({list.length} item{list.length !== 1 ? 's' : ''})</span>
                    <span>Rs. {totalAmount}</span>
                </div>
                <div className="d-flex justify-content-between mb-2">
                    <span className="text-muted">Total Units</span>
                    <span>{totalQuantity}</span>
                </div>
                <div className="d-flex justify-content-between mb-2">
                    <span className="text-muted">Delivery Charges</span>
                    <span className="text-success">FREE</span>
                </div>
                <hr className="my-2" />
                <div className="d-flex justify-content-between fw-bold fs-5">
                    <span>Total Amount</span>
                    <span style={{ color: '#2874F0' }}>Rs. {totalAmount}</span>
                </div>
                <hr className="my-2" />
                <div className="d-grid mt-3">
                    <button type="button" className="btn fw-bold text-white"
                        style={{ backgroundColor: '#FB641B', borderRadius: 2, padding: '0.65rem 0', letterSpacing: 0.4 }}>
                        PAYMENT
                    </button>
                </div>
            </div>
        </div>
    )
}
export default Stat
