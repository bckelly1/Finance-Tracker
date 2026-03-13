import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, NavLink } from 'react-router-dom';
import Transactions from './pages/Transactions';
import Accounts from './pages/Accounts';
import Budgets from './pages/Budgets';
import Categories from './pages/Categories';
import Institutions from './pages/Institutions';
import UnknownTransactions from './pages/UnknownTransactions';
import Vendors from './pages/Vendors';
import './App.css';

function App() {
    const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

    const toggleSidebar = () => {
        setSidebarCollapsed(!sidebarCollapsed);
    };

    return (
        <Router>
            <div className="app">
                <nav className={`sidebar ${sidebarCollapsed ? 'collapsed' : ''}`}>
                    <div className="sidebar-header">
                        <h1 className="app-title">Finance Tracker</h1>
                        <div className="app-subtitle">Manage your finances</div>
                    </div>

                    <div className="nav-links">
                        <NavLink to="/transactions" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            <span className="nav-icon">💳</span>
                            <span className="nav-text">Transactions</span>
                        </NavLink>

                        <NavLink to="/unknown-transactions" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            <span className="nav-icon">❓</span>
                            <span className="nav-text">Unknown Transactions</span>
                        </NavLink>

                        <NavLink to="/budgets" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            <span className="nav-icon">💰</span>
                            <span className="nav-text">Budgets</span>
                        </NavLink>

                        <NavLink to="/accounts" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            <span className="nav-icon">🏦</span>
                            <span className="nav-text">Accounts</span>
                        </NavLink>

                        <NavLink to="/categories" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            <span className="nav-icon">📊</span>
                            <span className="nav-text">Categories</span>
                        </NavLink>

                        <NavLink to="/vendors" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            <span className="nav-icon">🏪</span>
                            <span className="nav-text">Vendors</span>
                        </NavLink>

                        <NavLink to="/institutions" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                            <span className="nav-icon">🏛️</span>
                            <span className="nav-text">Institutions</span>
                        </NavLink>
                    </div>

                    <button className="sidebar-toggle" onClick={toggleSidebar}>
                        <span className="toggle-icon">{sidebarCollapsed ? '▶' : '◀'}</span>
                    </button>
                </nav>

                <main className={`main-content ${sidebarCollapsed ? 'sidebar-collapsed' : ''}`}>
                    <Routes>
                        <Route path="/" element={<Transactions />} />
                        <Route path="/transactions" element={<Transactions />} />
                        <Route path="/unknown-transactions" element={<UnknownTransactions />} />
                        <Route path="/budgets" element={<Budgets />} />
                        <Route path="/accounts" element={<Accounts />} />
                        <Route path="/categories" element={<Categories />} />
                        <Route path="/vendors" element={<Vendors />} />
                        <Route path="/institutions" element={<Institutions />} />
                    </Routes>
                </main>
            </div>
        </Router>
    );
}

export default App;