import React, { useState, useEffect } from 'react';

function Institutions() {
    const [institutions, setInstitutions] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchInstitutions();
    }, []);

    const fetchInstitutions = async () => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/institutions/`);
            if (!response.ok) throw new Error('Failed to fetch institutions');
            const data = await response.json();
            setInstitutions(Array.isArray(data) ? data : []);
        } catch (err) {
            // Mock data for demonstration
            setInstitutions([
                { id: 1, name: 'Chase Bank', type: 'Bank', accountCount: 2 },
                { id: 2, name: 'American Express', type: 'Credit Card', accountCount: 1 },
                { id: 3, name: 'Vanguard', type: 'Investment', accountCount: 1 }
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
                    <div className="loading-text">Loading institutions...</div>
                </div>
            </div>
        );
    }

    return (
        <div className="page-container">
            <div className="page-header">
                <h1 className="page-title">Institutions</h1>
                <p className="page-description">Financial institutions and providers</p>
            </div>

            <div className="table-container">
                <div className="table-wrapper">
                    <table className="data-table">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Institution Name</th>
                            <th>Type</th>
                            <th className="text-right">Account Count</th>
                        </tr>
                        </thead>
                        <tbody>
                        {institutions.map((institution) => (
                            <tr key={institution.id}>
                                <td className="font-medium">#{institution.id}</td>
                                <td>{institution.name}</td>
                                <td>{institution.type}</td>
                                <td className="text-right">{institution.accountCount}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}

export default Institutions;