import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './i18n';

import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import Dashboard from './pages/dashboard/Dashboard';
import CreateAccount from './pages/accounts/CreateAccount';
import EditAccount from './pages/accounts/EditAccount';
import Transactions from './pages/transactions/Transactions';
import CreateTransaction from './pages/transactions/CreateTransaction';
import CreateTransfer from './pages/transactions/CreateTransfer';
import Categories from './pages/categories/Categories';
import CreateCategory from './pages/categories/CreateCategory';
import EditCategory from './pages/categories/EditCategory';
import Merchants from './pages/merchants/Merchants';
import CreateMerchant from './pages/merchants/CreateMerchant';
import EditMerchant from './pages/merchants/EditMerchant';
import Profile from './pages/profile/Profile';

import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';

function App() {
  return (
    <>
      <ToastContainer position="bottom-right" theme="dark" />
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          <Route path="/*" element={
              <ProtectedRoute>
                  <Layout>
                      <Routes>
                          <Route path="/dashboard" element={<Dashboard />} />
                          
                          <Route path="/accounts/new" element={<CreateAccount />} />
                          <Route path="/accounts/edit/:id" element={<EditAccount />} />
                          
                          <Route path="/transactions" element={<Transactions />} />
                          <Route path="/transactions/new" element={<CreateTransaction />} />
                          <Route path="/transactions/transfer" element={<CreateTransfer />} />
                          
                          <Route path="/categories" element={<Categories />} />
                          <Route path="/categories/new" element={<CreateCategory />} />
                          <Route path="/categories/edit/:id" element={<EditCategory />} />
                          
                          <Route path="/merchants" element={<Merchants />} />
                          <Route path="/merchants/new" element={<CreateMerchant />} />
                          <Route path="/merchants/edit/:id" element={<EditMerchant />} />

                          <Route path="/profile" element={<Profile />} />
                          
                          <Route path="*" element={<Navigate to="/dashboard" replace />} />
                      </Routes>
                  </Layout>
              </ProtectedRoute>
          } />
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;