import { useEffect, useState } from "react"
import { useSelector } from "react-redux"

function Stat(){
    const list = useSelector(state=>state.cartItems)
    const totalQuantity = list.reduce((sum, c)=> sum + c.quantity, 0);
    const totalAmount = list.reduce((sum, c)=> sum + c.lineTotal, 0);

    return(
        <div>
            Total Items in Cart: {list.length} ({totalQuantity} units) <br />
            Total Amount: Rs. {totalAmount}
        </div>
    )
}
export default Stat
