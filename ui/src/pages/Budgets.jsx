import React, { useState, useEffect } from 'react';
import BudgetModal from '../components/BudgetModal';
import CreateBudgetModal from '../components/CreateBudgetModal';
import './Budgets.css';

function Budgets() {
    const [budgets, setBudgets] = useState([]);
    const [categories, setCategories] = useState([]);
    const [accounts, setAccounts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedBudget, setSelectedBudget] = useState(null);
    const [showCreateModal, setShowCreateModal] = useState(false);

    useEffect(() => {
        fetchBudgets();
        fetchCategories();
        fetchAccounts();
    }, []);

    const fetchBudgets = async () => {
        try {
            setLoading(true);
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/budgets/all`);
            if (!response.ok) {
                throw new Error('Failed to fetch budgets');
            }
            const data = await response.json();
            setBudgets(Array.isArray(data) ? data : []);
            setError(null);
        } catch (err) {
            setError(err.message);
            // Mock data for demonstration
            setBudgets([
                {
                    id: 1,
                    name: 'Groceries Budget',
                    limitAmount: 500.00,
                    spent: 342.50,
                    remaining: 157.50,
                    percentUsed: 68.5,
                    isOverBudget: false
                },
                {
                    id: 2,
                    name: 'Dining Out',
                    limitAmount: 200.00,
                    spent: 225.80,
                    remaining: -25.80,
                    percentUsed: 112.9,
                    isOverBudget: true
                },
                {
                    id: 3,
                    name: 'Entertainment',
                    limitAmount: 150.00,
                    spent: 45.00,
                    remaining: 105.00,
                    percentUsed: 30.0,
                    isOverBudget: false
                }
            ]);
        } finally {
            setLoading(false);
        }
    };

    const fetchCategories = async () => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/categories/`);
            if (!response.ok) throw new Error('Failed to fetch categories');
            const data = await response.json();
            setCategories(Array.isArray(data) ? data : []);
        } catch (err) {
            setCategories([
                { id: 1, name: 'Groceries' },
                { id: 2, name: 'Dining' },
                { id: 3, name: 'Entertainment' },
                { id: 4, name: 'Transportation' },
                { id: 5, name: 'Utilities' }
            ]);
        }
    };

    const fetchAccounts = async () => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/accounts/`);
            if (!response.ok) throw new Error('Failed to fetch accounts');
            const data = await response.json();
            setAccounts(Array.isArray(data) ? data : []);
        } catch (err) {
            setAccounts([
                { id: 1, name: 'Checking Account' },
                { id: 2, name: 'Credit Card' }
            ]);
        }
    };

    const handleRowClick = (budget) => {
        setSelectedBudget(budget);
    };

    const handleCloseModal = () => {
        setSelectedBudget(null);
    };

    const handleSaveBudget = async (id, updatedData) => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/budgets`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ ...updatedData, id }),
            });

            if (!response.ok) {
                throw new Error('Failed to update budget');
            }

            await fetchBudgets(); // Refresh to get updated spent/remaining values
            handleCloseModal();
        } catch (err) {
            console.error('Error updating budget:', err);
            alert('Failed to update budget');
        }
    };

    const handleDeleteBudget = async (id) => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/budgets/${id}`, {
                method: 'DELETE',
            });

            if (!response.ok) {
                throw new Error('Failed to delete budget');
            }

            setBudgets(prev => prev.filter(b => b.id !== id));
            handleCloseModal();
        } catch (err) {
            console.error('Error deleting budget:', err);
            alert('Failed to delete budget');
        }
    };

    const handleCreateBudget = async (budgetData) => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/budgets`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(budgetData),
            });

            if (!response.ok) {
                throw new Error('Failed to create budget');
            }

            await fetchBudgets(); // Refresh list
            setShowCreateModal(false);
            alert('Budget created successfully!');
        } catch (err) {
            console.error('Error creating budget:', err);
            alert('Failed to create budget');
        }
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
        }).format(amount);
    };

    const getProgressBarColor = (percentUsed, isOverBudget) => {
        if (isOverBudget) return 'over-budget';
        if (percentUsed >= 90) return 'warning';
        if (percentUsed >= 75) return 'caution';
        return 'healthy';
    };

    if (loading) {
        return (
            <div className="page-container">
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <div className="loading-text">Loading budgets...</div>
                </div>
            </div>
        );
    }

    return (
        <div className="page-container">
            <div className="page-header">
                <div>
                    <h1 className="page-title">Budgets</h1>
                    <p className="page-description">Track spending against your budget limits</p>
                </div>
                <button
                    className="btn-create"
                    onClick={() => setShowCreateModal(true)}
                >
                    + New Budget
                </button>
            </div>

            {error && (
                <div className="error-banner">
                    <span className="error-icon">⚠️</span>
                    <span>API connection failed. Showing demo data.</span>
                </div>
            )}

            <div className="budgets-grid">
                {budgets.length === 0 ? (
                    <div className="empty-state">
                        <div className="empty-icon">💰</div>
                        <div className="empty-text">No budgets found</div>
                        <button
                            className="btn-create"
                            onClick={() => setShowCreateModal(true)}
                        >
                            Create Your First Budget
                        </button>
                    </div>
                ) : (
                    budgets.map((budget) => (
                        <div
                            key={budget.id}
                            className="budget-card"
                            onClick={() => handleRowClick(budget)}
                        >
                            <div className="budget-header">
                                <h3 className="budget-name">{budget.name}</h3>
                                {budget.isOverBudget && (
                                    <span className="over-budget-badge">Over Budget</span>
                                )}
                            </div>

                            <div className="budget-amounts">
                                <div className="budget-amount-item">
                                    <span className="amount-label">Spent</span>
                                    <span className={`amount-value ${budget.isOverBudget ? 'over' : ''}`}>
                    {formatCurrency(budget.spent)}
                  </span>
                                </div>
                                <div className="budget-amount-item">
                                    <span className="amount-label">Limit</span>
                                    <span className="amount-value">
                    {formatCurrency(budget.limitAmount)}
                  </span>
                                </div>
                                <div className="budget-amount-item">
                                    <span className="amount-label">Remaining</span>
                                    <span className={`amount-value ${budget.remaining < 0 ? 'negative' : 'positive'}`}>
                    {formatCurrency(budget.remaining)}
                  </span>
                                </div>
                            </div>

                            <div className="progress-container">
                                <div className="progress-bar-wrapper">
                                    <div
                                        className={`progress-bar ${getProgressBarColor(budget.percentUsed, budget.isOverBudget)}`}
                                        style={{ width: `${Math.min(budget.percentUsed, 100)}%` }}
                                    ></div>
                                </div>
                                <div className="progress-text">
                                    {budget.percentUsed.toFixed(1)}% used
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>

            {selectedBudget && (
                <BudgetModal
                    budget={selectedBudget}
                    categories={categories}
                    accounts={accounts}
                    onClose={handleCloseModal}
                    onSave={handleSaveBudget}
                    onDelete={handleDeleteBudget}
                />
            )}

            {showCreateModal && (
                <CreateBudgetModal
                    categories={categories}
                    accounts={accounts}
                    onClose={() => setShowCreateModal(false)}
                    onCreate={handleCreateBudget}
                />
            )}
        </div>
    );
}

export default Budgets;