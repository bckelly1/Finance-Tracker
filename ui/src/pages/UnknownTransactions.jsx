import React, { useState, useEffect } from 'react';

function UnknownTransactions() {
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchUnknownTransactions();
    }, []);

    const fetchUnknownTransactions = async () => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/transactions/unknown`);
            if (!response.ok) throw new Error('Failed to fetch unknown transactions');
            const data = await response.json();
            setTransactions(Array.isArray(data) ? data : []);
        } catch (err) {
            // Mock data for demonstration
            setTransactions([
                { id: 1, date: '2024-02-14', description: 'UNKNOWN MERCHANT 123', amount: -45.67 },
                { id: 2, date: '2024-02-13', description: 'ACH TRANSFER XYZ', amount: -120.00 },
                { id: 3, date: '2024-02-12', description: 'CHECK 4567', amount: -89.99 }
            ]);
        } finally {
            setLoading(false);
        }
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(Math.abs(amount));
    };

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
        });
    };

    if (loading) {
        return (
            <div className="page-container">
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <div className="loading-text">Loading unknown transactions...</div>
                </div>
            </div>
        );
    }

    return (
        <div className="page-container">
            <div className="page-header">
                <h1 className="page-title">Unknown Transactions</h1>
                <p className="page-description">Transactions requiring categorization</p>
            </div>

            <div className="table-container">
                <div className="table-wrapper">
                    <table className="data-table">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Date</th>
                            <th>Description</th>
                            <th className="text-right">Amount</th>
                        </tr>
                        </thead>
                        <tbody>
                        {transactions.length === 0 ? (
                            <tr>
                                <td colSpan="4" className="empty-state">
                                    <div className="empty-icon">✅</div>
                                    <div className="empty-text">All transactions categorized!</div>
                                </td>
                            </tr>
                        ) : (
                            transactions.map((transaction) => (
                                <tr key={transaction.id}>
                                    <td className="font-medium">#{transaction.id}</td>
                                    <td>{formatDate(transaction.date)}</td>
                                    <td>{transaction.description}</td>
                                    <td className="text-right font-semibold amount-negative">
                                        {formatCurrency(transaction.amount)}
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}

export default UnknownTransactions;