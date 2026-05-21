import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { axiosInstance } from '../../api/axiosInstance';
import { useTranslation } from 'react-i18next';
import { toast } from 'react-toastify';

export default function CreateTransfer() {
    const { t } = useTranslation();
    const navigate = useNavigate();

    const [accounts, setAccounts] = useState<any[]>([]);
    const [accountId, setAccountId] = useState('');
    const [targetAccountId, setTargetAccountId] = useState('');
    const [title, setTitle] = useState('');
    const [amount, setAmount] = useState('');
    const [description, setDescription] = useState('');

    useEffect(() => {
        axiosInstance.get('/accounts').then(res => setAccounts(res.data));
    }, []);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        
        if (accountId === targetAccountId) {
            toast.error(t('transfers.same_account_error', 'Рахунки не можуть бути однаковими'));
            return;
        }

        try {
            await axiosInstance.post('/transactions/transfers', {
                accountId: parseInt(accountId),
                targetAccountId: parseInt(targetAccountId),
                title,
                amount: parseFloat(amount),
                description: description || null
            });
            toast.success(t('transfers.create_success', 'Переказ успішно виконано!'));
            navigate('/transactions'); 
        } catch (error: any) {
            toast.error(error.response?.data?.message || t('transfers.create_error', 'Помилка при переказі'));
        }
    };

    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh', width: '100%' }}>
            <div style={{ color: '#e0e0e0', fontFamily: 'sans-serif', maxWidth: '600px', width: '100%', padding: '20px' }}>
                <h2 style={{ marginBottom: '20px', textAlign: 'center' }}>{t('transfers.new', 'Новий переказ')}</h2>
                
                <form onSubmit={handleSubmit} style={{ 
                    display: 'flex', flexDirection: 'column', gap: '15px', 
                    background: '#1e1e1e', padding: '25px', borderRadius: '8px', 
                    border: '1px solid #333', boxShadow: '0 4px 15px rgba(0,0,0,0.3)' 
                }}>
                    
                    <input type="text" placeholder={t('transfers.title', 'Назва')} value={title} onChange={e => setTitle(e.target.value)} required style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff' }} />

                    <select value={accountId} onChange={e => setAccountId(e.target.value)} required style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', cursor: 'pointer' }}>
                        <option value="">{t('transfers.from_account', 'З якого рахунку')}</option>
                        {accounts.map(a => <option key={a.id} value={a.id}>{a.title}</option>)}
                    </select>

                    <select value={targetAccountId} onChange={e => setTargetAccountId(e.target.value)} required style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', cursor: 'pointer' }}>
                        <option value="">{t('transfers.to_account', 'На який рахунок')}</option>
                        {accounts.map(a => <option key={a.id} value={a.id}>{a.title}</option>)}
                    </select>

                    <input type="number" step="0.01" placeholder={t('transfers.amount', 'Сума')} value={amount} onChange={e => setAmount(e.target.value)} required style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff' }} />

                    <textarea placeholder={t('transfers.description', 'Опис')} value={description} onChange={e => setDescription(e.target.value)} style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', minHeight: '80px', resize: 'none' }} />

                    <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                        <button type="submit" style={{ flex: 1, padding: '12px', background: '#4CAF50', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>
                            {t('common.create', 'Переказати')}
                        </button>
                        <button type="button" onClick={() => navigate('/transactions')} style={{ padding: '12px', background: '#444', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' }}>
                            {t('common.cancel', 'Скасувати')}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}