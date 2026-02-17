import React, { useState, useEffect } from 'react';
import TransactionModal from '../components/TransactionModal';
import './Transactions.css';

function Transactions() {
    const [transactions, setTransactions] = useState([]);
    const [selectedTransaction, setSelectedTransaction] = useState(null);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [filterCategory, setFilterCategory] = useState('all');
    const [sortField, setSortField] = useState('date');
    const [sortDirection, setSortDirection] = useState('desc');

    useEffect(() => {
        fetchTransactions();
        fetchCategories();
    }, []);

    const fetchTransactions = async () => {
        try {
            setLoading(true);
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/transactions/`);
            if (!response.ok) {
                throw new Error('Failed to fetch transactions');
            }
            const responseText = await response.text();
            const data = JSON.parse(responseText);
            setTransactions(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const fetchCategories = async () => {
        const response = await fetch(`${process.env.REACT_APP_API_URL}/api/categories/`);
        if (!response.ok) {
            throw new Error('Failed to fetch categories');
        }
        const data = await response.json();
        setCategories(Array.isArray(data) ? data : []);
    };

    const handleCategoryChange = async (transactionId, newCategory) => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/transactions/${transactionId}/`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ category: newCategory }),
            });

            if (!response.ok) {
                throw new Error('Failed to update category');
            }

            // Update local state
            setTransactions(prev =>
                prev.map(t =>
                    t.id === transactionId ? { ...t, category: newCategory } : t
                )
            );
        } catch (err) {
            console.error('Error updating category:', err);
            // For demo, still update locally
            setTransactions(prev =>
                prev.map(t =>
                    t.id === transactionId ? { ...t, category: newCategory } : t
                )
            );
        }
    };

    const handleSort = (field) => {
        if (sortField === field) {
            setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
        } else {
            setSortField(field);
            setSortDirection('asc');
        }
    };

    const filteredAndSortedTransactions = transactions
        .filter(t => {
            const matchesSearch = t.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
                t.vendor?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                t.notes?.toLowerCase().includes(searchTerm.toLowerCase());
            const matchesCategory = filterCategory === 'all' || t.category === filterCategory;
            return matchesSearch && matchesCategory;
        })
        .sort((a, b) => {
            let aVal = a[sortField];
            let bVal = b[sortField];

            if (sortField === 'date') {
                aVal = new Date(aVal);
                bVal = new Date(bVal);
            }

            if (sortField === 'amount') {
                aVal = parseFloat(aVal);
                bVal = parseFloat(bVal);
            }

            if (aVal < bVal) return sortDirection === 'asc' ? -1 : 1;
            if (aVal > bVal) return sortDirection === 'asc' ? 1 : -1;
            return 0;
        });

    const formatCurrency = (amount) => {
        const absAmount = Math.abs(amount);
        const formatted = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
        }).format(absAmount);
        return amount < 0 ? `-${formatted}` : formatted;
    };

    const formatDate = (dateString) => {
        // Parse as UTC, then display in local timezone
        const date = new Date(dateString + ' UTC');

        return date.toLocaleString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: 'numeric',
            minute: 'numeric',
            second: 'numeric',
            hour12: true,
            timeZoneName: 'short' // Optional: shows timezone abbreviation like "MST"
        });
    };

    const getSortIcon = (field) => {
        if (sortField !== field) return '↕️';
        return sortDirection === 'asc' ? '↑' : '↓';
    };

    const handleRowClick = (transaction) => {
        setSelectedTransaction(transaction);
    };

    const handleCloseModal = () => {
        setSelectedTransaction(null);
    };

    const handleSaveTransaction = async (id, updatedData) => {
        const date = new Date(updatedData.date);
        const formattedDate = date.getFullYear() + '-' +
            String(date.getMonth() + 1).padStart(2, '0') + '-' +
            String(date.getDate()).padStart(2, '0') + ' ' +
            String(date.getHours()).padStart(2, '0') + ':' +
            String(date.getMinutes()).padStart(2, '0') + ':' +
            String(date.getSeconds()).padStart(2, '0');
        const dataToSend = {
            ...updatedData,
            date: formattedDate
        };
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/transactions/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(dataToSend),
            });

            if (!response.ok) {
                throw new Error('Failed to update transaction');
            }

            // Update local state
            setTransactions(prev =>
                prev.map(t =>
                    t.id === id ? { ...t, ...dataToSend } : t
                )
            );

            handleCloseModal();
        } catch (err) {
            console.error('Error updating transaction:', err);
            alert('Failed to update transaction');
        }
    };

    const handleDeleteTransaction = async (id) => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/transactions/${id}`, {
                method: 'DELETE',
            });

            if (!response.ok) {
                throw new Error('Failed to delete transaction');
            }

            // Remove from local state
            setTransactions(prev => prev.filter(t => t.id !== id));

            handleCloseModal();
        } catch (err) {
            console.error('Error deleting transaction:', err);
            alert('Failed to delete transaction');
        }
    };

    if (loading) {
        return (
            <div className="page-container">
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <div className="loading-text">Loading transactions...</div>
                </div>
            </div>
        );
    }

    return (
        <div className="page-container">
            <div className="page-header">
                <h1 className="page-title">Transactions</h1>
                <p className="page-description">View and manage all your financial transactions</p>
            </div>

            <div className="transactions-controls">
                <div className="search-box">
                    <span className="search-icon">🔍</span>
                    <input
                        type="text"
                        placeholder="Search transactions..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="search-input"
                    />
                </div>

                <div className="filter-group">
                    <label className="filter-label">Category:</label>
                    <select
                        value={filterCategory}
                        onChange={(e) => setFilterCategory(e.target.value)}
                        className="filter-select"
                    >
                        <option value="all">All Categories</option>
                        {categories.map(cat => (
                            <option key={cat.id} value={cat.name}>{cat.name}</option>
                        ))}
                    </select>
                </div>
            </div>

            {error && (
                <div className="error-banner">
                    <span className="error-icon">⚠️</span>
                    <span>API connection failed. Showing demo data.</span>
                </div>
            )}

            <div className="transactions-summary">
                <div className="summary-card">
                    <div className="summary-label">Total Transactions</div>
                    <div className="summary-value">{filteredAndSortedTransactions.length}</div>
                </div>
                <div className="summary-card">
                    <div className="summary-label">Total Income</div>
                    <div className="summary-value positive">
                        {formatCurrency(filteredAndSortedTransactions
                            // .filter(t => t.amount > 0)
                            .filter(t => t.category !== "Transfer")
                            .filter(t => t.type === "Credit")
                            .reduce((sum, t) => sum + t.amount, 0))}
                    </div>
                </div>
                <div className="summary-card">
                    <div className="summary-label">Total Expenses</div>
                    <div className="summary-value negative">
                        {formatCurrency(filteredAndSortedTransactions
                            // .filter(t => t.amount < 0)
                            .filter(t => t.category !== "Transfer")
                            .filter(t => t.type === "Debit")
                            .reduce((sum, t) => sum + t.amount, 0))}
                    </div>
                </div>
            </div>

            <div className="table-container">
                <div className="table-wrapper">
                    <table className="data-table">
                        <thead>
                        <tr>
                            <th onClick={() => handleSort('id')} className="sortable">
                                ID {getSortIcon('id')}
                            </th>
                            <th onClick={() => handleSort('date')} className="sortable">
                                Date {getSortIcon('date')}
                            </th>
                            <th onClick={() => handleSort('description')} className="sortable">
                                Description {getSortIcon('description')}
                            </th>
                            <th onClick={() => handleSort('amount')} className="sortable text-right">
                                Amount {getSortIcon('amount')}
                            </th>
                            <th onClick={() => handleSort('type')} className="sortable">
                                Type {getSortIcon('type')}
                            </th>
                            <th>Category</th>
                            <th onClick={() => handleSort('vendor')} className="sortable">
                                Vendor {getSortIcon('vendor')}
                            </th>
                            <th>Notes</th>
                        </tr>
                        </thead>
                        <tbody>
                        {filteredAndSortedTransactions.length === 0 ? (
                            <tr>
                                <td colSpan="9" className="empty-state">
                                    <div className="empty-icon">📭</div>
                                    <div className="empty-text">No transactions found</div>
                                </td>
                            </tr>
                        ) : (
                            filteredAndSortedTransactions.map((transaction) => (
                                <tr
                                    key={transaction.id}
                                    onClick={() => handleRowClick(transaction)}
                                    style={{ cursor: 'pointer' }}
                                >
                                    <td className="font-medium">#{transaction.id}</td>
                                    <td>{formatDate(transaction.date)}</td>
                                    <td className="description-cell">{transaction.description}</td>
                                    <td className={`text-right font-semibold ${transaction.amount >= 0 ? 'amount-positive' : 'amount-negative'}`}>
                                        {formatCurrency(transaction.amount)}
                                    </td>
                                    <td>
                                      <span className={`type-badge ${transaction.type.toLowerCase()}`}>
                                        {transaction.type}
                                      </span>
                                    </td>
                                    <td>
                                        <select
                                            value={transaction.category || ''}
                                            onChange={(e) => handleCategoryChange(transaction.id, e.target.value)}
                                            className="category-select"
                                        >
                                            <option value="">Select Category</option>
                                            {categories.map(cat => (
                                                <option key={cat.id} value={cat.name}>{cat.name}</option>
                                            ))}
                                        </select>
                                    </td>
                                    <td>{transaction.vendor || '-'}</td>
                                    <td className="notes-cell">{transaction.notes || '-'}</td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
            </div>

            {selectedTransaction && (
                <TransactionModal
                    transaction={selectedTransaction}
                    categories={categories}
                    onClose={handleCloseModal}
                    onSave={handleSaveTransaction}
                    onDelete={handleDeleteTransaction}
                />
            )}
        </div>
    );
}

export default Transactions;