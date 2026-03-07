import './TransactionModal.css';

function ImportAccountBalancesModal({ onClose, onCreate }) {
    const handleSubmit = async (e) => {
        e.preventDefault();
        await onCreate();
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">
                    <h2>Import Account Balances</h2>
                    <button className="modal-close" onClick={onClose}>×</button>
                </div>

                <form onSubmit={handleSubmit} className="modal-form">
                    <div className="modal-actions">
                        <div className="modal-actions-right">
                            <button type="button" onClick={onClose} className="btn-secondary">
                                Cancel
                            </button>
                            <button type="submit" className="btn-primary">
                                Import Account Balances
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default ImportAccountBalancesModal;