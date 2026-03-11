import React, { useState, useEffect } from 'react';
import './TransactionModal.css';

function BudgetModal({ budget, categories, accounts, onClose, onSave, onDelete }) {
    const [formData, setFormData] = useState({
        name: '',
        limitAmount: '',
        period: 'MONTHLY',
        categoryId: '',
        accountId: '',
        startDate: ''
    });

    useEffect(() => {
        if (budget) {
            setFormData({
                name: budget.name || '',
                limitAmount: budget.limitAmount || '',
                period: budget.period || 'MONTHLY',
                categoryId: budget.categoryId || '',
                accountId: budget.accountId || '',
                startDate: budget.startDate || ''
            });
        }
    }, [budget]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        await onSave(budget.id, formData);
    };

    const handleDelete = async () => {
        if (window.confirm('Are you sure you want to delete this budget? This action cannot be undone.')) {
            await onDelete(budget.id);
        }
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
        }).format(amount);
    };

    if (!budget) return null;

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">
                    <h2>Edit Budget</h2>
                    <button className="modal-close" onClick={onClose}>×</button>
                </div>

                <div className="budget-summary">
                    <div className="summary-item">
                        <span className="summary-label">Spent</span>
                        <span className="summary-value">{formatCurrency(budget.spent)}</span>
                    </div>
                    <div className="summary-item">
                        <span className="summary-label">Remaining</span>
                        <span className={`summary-value ${budget.remaining < 0 ? 'negative' : 'positive'}`}>
              {formatCurrency(budget.remaining)}
            </span>
                    </div>
                    <div className="summary-item">
                        <span className="summary-label">Progress</span>
                        <span className="summary-value">{budget.percentUsed.toFixed(1)}%</span>
                    </div>
                </div>

                <form onSubmit={handleSubmit} className="modal-form">
                    <div className="form-group">
                        <label>Budget ID</label>
                        <input type="text" value={budget.id} disabled />
                    </div>

                    <div className="form-group">
                        <label>Budget Name *</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Limit Amount *</label>
                            <input
                                type="number"
                                step="0.01"
                                name="limitAmount"
                                value={formData.limitAmount}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label>Period *</label>
                            <select
                                name="period"
                                value={formData.period}
                                onChange={handleChange}
                                required
                            >
                                <option value="WEEKLY">Weekly</option>
                                <option value="MONTHLY">Monthly</option>
                                <option value="YEARLY">Yearly</option>
                            </select>
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Category</label>
                            <select
                                name="categoryId"
                                value={formData.categoryId}
                                onChange={handleChange}
                            >
                                <option value="">Select Category</option>
                                {categories.map(cat => (
                                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                                ))}
                            </select>
                        </div>

                        <div className="form-group">
                            <label>Account</label>
                            <select
                                name="accountId"
                                value={formData.accountId}
                                onChange={handleChange}
                            >
                                <option value="">Select Account</option>
                                {accounts.map(acc => (
                                    <option key={acc.id} value={acc.id}>{acc.name}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Start Date</label>
                        <input
                            type="date"
                            name="startDate"
                            value={formData.startDate}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="modal-actions">
                        <button
                            type="button"
                            onClick={handleDelete}
                            className="btn-danger"
                        >
                            Delete
                        </button>
                        <div className="modal-actions-right">
                            <button type="button" onClick={onClose} className="btn-secondary">
                                Cancel
                            </button>
                            <button type="submit" className="btn-primary">
                                Save Changes
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default BudgetModal;