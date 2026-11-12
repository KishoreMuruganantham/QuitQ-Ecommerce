import { Route, Routes } from "react-router"
import Login from "./components/auth/Login"
import SignUp from "./components/customer/SignUp"
import ForgotPassword from "./components/auth/ForgotPassword"
import ResetPassword from "./components/auth/ResetPassword"
import PageNotFound from "./components/PageNotFound"
import CustomerLayout from "./components/layout/CustomerLayout"
import SellerLayout from "./components/layout/SellerLayout"
import AdminLayout from "./components/layout/AdminLayout"
import Home from "./components/customer/Home"
import ProductDetail from "./components/customer/ProductDetail"
import Cart from "./components/customer/Cart"
import Checkout from "./components/customer/Checkout"
import OrderConfirmation from "./components/customer/OrderConfirmation"
import MyOrders from "./components/customer/MyOrders"
import OrderTracking from "./components/customer/OrderTracking"
import MyProducts from "./components/seller/MyProducts"
import SellerOrders from "./components/seller/SellerOrders"
import SalesReport from "./components/seller/SalesReport"
import Analytics from "./components/admin/Analytics"
import ManageUsers from "./components/admin/ManageUsers"
import ManageCategories from "./components/admin/ManageCategories"
import BookList from "./components/library/BookList"

function App() {
return(
  <Routes>
    <Route path="" element={<Login />}/>
    <Route path="library" element={<BookList />}/>
    <Route path="customer/signup" element={<SignUp />}/>
    <Route path="forgot-password" element={<ForgotPassword />}/>
    <Route path="reset-password" element={<ResetPassword />}/>
    <Route path="customer/:token" element={<CustomerLayout />}>
      <Route index element={<Home />} />
      <Route path="product/:id" element={<ProductDetail />} />
      <Route path="cart" element={<Cart />} />
      <Route path="checkout" element={<Checkout />} />
      <Route path="order-confirmation/:id" element={<OrderConfirmation />} />
      <Route path="orders" element={<MyOrders />} />
      <Route path="orders/:id" element={<OrderTracking />} />
    </Route>
    <Route path="seller/:token" element={<SellerLayout />}>
      <Route index element={<MyProducts />} />
      <Route path="orders" element={<SellerOrders />} />
      <Route path="sales" element={<SalesReport />} />
    </Route>
    <Route path="admin/:token" element={<AdminLayout />}>
      <Route index element={<Analytics />} />
      <Route path="users" element={<ManageUsers />} />
      <Route path="categories" element={<ManageCategories />} />
    </Route>
    <Route path="*" element={<PageNotFound />}/>
  </Routes>
) 
  
}

export default App
