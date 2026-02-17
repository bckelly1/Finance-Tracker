import React, { useState, useEffect } from 'react';

function Vendors() {
    const [vendors, setVendors] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchVendors();
    }, []);

    const fetchVendors = async () => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/vendors/`);
            if (!response.ok) throw new Error('Failed to fetch vendors');
            const data = await response.json();
            setVendors(Array.isArray(data) ? data : []);
        } catch (err) {
            // Mock data for demonstration
            setVendors([
                { id: 1, name: 'Whole Foods', category: 'Groceries', transactionCount: 24, totalSpent: 2156.78 },
                { id: 2, name: 'Shell', category: 'Transportation', transactionCount: 18, totalSpent: 856.40 },
                { id: 3, name: 'Amazon', category: 'Shopping', transactionCount: 45, totalSpent: 3421.50 },
                { id: 4, name: 'Netflix', category: 'Entertainment', transactionCount: 12, totalSpent: 179.88 }
            ]);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="page-container">
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <div className="loading-text">Loading vendors...</div>
                </div>
            </div>
        );
    }

    return (
        <div className="page-container">
            <div className="page-header">
                <h1 className="page-title">Vendors</h1>
                <p className="page-description">Merchants and service providers</p>
            </div>

            <div className="table-container">
                <div className="table-wrapper">
                    <table className="data-table">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Vendor Name</th>
                            <th>Category</th>
                            <th className="text-right">Transactions</th>
                            <th className="text-right">Total Spent</th>
                        </tr>
                        </thead>
                        <tbody>
                        {vendors.map((vendor) => (
                            <tr key={vendor.id}>
                                <td className="font-medium">#{vendor.id}</td>
                                <td>{vendor.name}</td>
                                <td>{vendor.category}</td>
                                <td className="text-right">{vendor.transactionCount}</td>
                                <td className="text-right font-semibold">
                                    {new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(vendor.totalSpent)}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}

export default Vendors;