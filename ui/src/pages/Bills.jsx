import React, { useState, useEffect } from 'react';
import CreateBillModal from '../components/CreateBillModal';
import './Bills.css';
import BillModal from "../components/BillModal";

function Bills() {
    const [bills, setBills] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedBill, setSelectedBill] = useState(null);
    const [showCreateModal, setShowCreateModal] = useState(false);

    useEffect(() => {
        fetchBills();
    }, []);

    const fetchBills = async () => {
        try {
            setLoading(true);
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/bills/all`);
            if (!response.ok) {
                throw new Error('Failed to fetch bills');
            }
            const data = await response.json();
            setBills(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            setError(err.message);
            console.error("Error fetching bills", err);
        } finally {
            setLoading(false);
        }
    };

    const handleRowClick = (bill) => {
        setSelectedBill(bill);
    };

    const handleCloseModal = () => {
        setSelectedBill(null);
    };

    const handleSaveBill = async (id, updatedData) => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/bills`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ ...updatedData, id }),
            });

            if (!response.ok) {
                throw new Error('Failed to update bill');
            }

            await fetchBills();
            handleCloseModal();
        } catch (err) {
            console.error('Error updating bill:', err);
            alert('Failed to update bill');
        }
    };

    const handleDeleteBill = async (id) => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/bills/${id}`, {
                method: 'DELETE',
            });

            if (!response.ok) {
                throw new Error('Failed to delete bill');
            }

            setBills(prev => prev.filter(b => b.id !== id));
            handleCloseModal();
        } catch (err) {
            console.error('Error deleting bill:', err);
            alert('Failed to delete bill');
        }
    };

    const handleCreateBill = async (billData) => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/bills`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(billData),
            });

            if (!response.ok) {
                throw new Error('Failed to create bill');
            }

            await fetchBills();
            setShowCreateModal(false);
            alert('Bill created successfully!');
        } catch (err) {
            console.error('Error creating bill:', err);
            alert('Failed to create bill');
        }
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
        }).format(amount);
    };

    const formatDate = (dateString) => {
        // Parse as UTC, then display in local timezone
        const date = new Date(dateString + ' UTC');

        return date.toLocaleString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    };

    if (loading) {
        return (
            <div className="page-container">
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <div className="loading-text">Loading bills...</div>
                </div>
            </div>
        );
    }

    return (
        <div className="page-container">
            <div className="page-header">
                <div>
                    <h1 className="page-title">Bills</h1>
                    <p className="page-description">Track your bills</p>
                </div>
                <button
                    className="btn-create"
                    onClick={() => setShowCreateModal(true)}
                >
                    + New Bill
                </button>
            </div>

            {error && (
                <div className="error-banner">
                    <span className="error-icon">⚠️</span>
                    <span>API connection failed.</span>
                </div>
            )}

            <div className="bills-grid">
                {bills.length === 0 ? (
                    <div className="empty-state">
                        <div className="empty-icon">💰</div>
                        <div className="empty-text">No bills found</div>
                        <button
                            className="btn-create"
                            onClick={() => setShowCreateModal(true)}
                        >
                            Create Your First Bill
                        </button>
                    </div>
                ) : (
                    bills.map((bill) => (
                        <div
                            key={bill.id}
                            className="bill-card"
                            onClick={() => handleRowClick(bill)}
                        >
                            <div className="bill-header">
                                <div>
                                    <h3 className="bill-name">{bill.name}</h3>
                                    {bill.transactionDescription && (
                                        <p className="bill-description">{bill.transactionDescription}</p>
                                    )}
                                </div>
                                {bill.billArrived && (
                                    <span className="over-budget-badge">Bill Arrived</span>
                                )}
                            </div>

                            <div className="bill-amounts">
                                <div className="bill-amount-item">
                                    <span className="amount-label">Amount</span>
                                    <span className={`amount-value ${bill.billArrived ? 'arrived' : ''}`}>
                                        {formatCurrency(bill.amount)}
                                    </span>
                                </div>
                                <div className="bill-date-item">
                                    <span className="date-label">Arrival</span>
                                    <span className="date-value">
                                        {formatDate(bill.date)}
                                    </span>
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>

            {selectedBill && (
                <BillModal
                    bill={selectedBill}
                    onClose={handleCloseModal}
                    onSave={handleSaveBill}
                    onDelete={handleDeleteBill}
                />
            )}

            {showCreateModal && (
                <CreateBillModal
                    onClose={() => setShowCreateModal(false)}
                    onCreate={handleCreateBill}
                />
            )}
        </div>
    );
}

export default Bills;