import React, { useState, useEffect } from 'react';
import ImportAccountBalancesModal from "../components/ImportMailBalancesModal";

function Accounts() {
    const [accounts, setAccounts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showImportBalancesModal, setShowImportBalancesModal] = useState(false);

    useEffect(() => {
        fetchAccounts();
    }, []);

    const fetchAccounts = async () => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/accounts/`);
            if (!response.ok) throw new Error('Failed to fetch accounts');
            const data = await response.json();
            setAccounts(Array.isArray(data) ? data : []);
        } catch (err) {
            // Mock data for demonstration
            setAccounts([
                { id: 1, name: 'Checking Account', institution: 'Chase Bank', balance: 5234.56, type: 'Checking' },
                { id: 2, name: 'Savings Account', institution: 'Chase Bank', balance: 15000.00, type: 'Savings' },
                { id: 3, name: 'Credit Card', institution: 'American Express', balance: -1250.43, type: 'Credit' }
            ]);
        } finally {
            setLoading(false);
        }
    };

    const handleImportAccountBalances = async () => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/accounts/balances/import`, {
                method: 'GET'
            });

            if (!response.ok) {
                alert('Failed to import account balances');
                console.log(response)
                throw new Error('Failed to import account balances');
            }

            const importResponse = await response.json();
            console.log(importResponse);

            setShowImportBalancesModal(false);

            alert('Balances imported from mail successfully!');
        } catch (err) {
            console.error('Error importing account balances:', err);
            alert('Failed to import account balances');
        }
    };

    if (loading) {
        return (
            <div className="page-container">
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <div className="loading-text">Loading accounts...</div>
                </div>
            </div>
        );
    }

    return (
        <div className="page-container">
            <div className="page-header">
                <h1 className="page-title">Accounts</h1>
                <p className="page-description">Manage your financial accounts</p>

                <div className="header-actions">
                    <button
                        className="btn-import"
                        onClick={() => setShowImportBalancesModal(true)}
                        title="Import From Mail"
                    >
                        📥 Import From Mail
                    </button>
                </div>
            </div>

            <div className="table-container">
                <div className="table-wrapper">
                    <table className="data-table">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Account Name</th>
                            <th>Institution</th>
                            <th>Type</th>
                            <th className="text-right">Balance</th>
                        </tr>
                        </thead>
                        <tbody>
                        {accounts.map((account) => (
                            <tr key={account.id}>
                                <td className="font-medium">#{account.id}</td>
                                <td>{account.name}</td>
                                <td>{account.institution}</td>
                                <td>{account.type}</td>
                                <td className={`text-right font-semibold ${account.balance >= 0 ? 'amount-positive' : 'amount-negative'}`}>
                                    {new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(account.balance)}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
            {showImportBalancesModal && (
                <ImportAccountBalancesModal
                    onClose={() => setShowImportBalancesModal(false)}
                    onCreate={handleImportAccountBalances}
                />
            )}
        </div>
    );
}

export default Accounts;