import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { axiosInstance } from '../../api/axiosInstance';
import { useTranslation } from 'react-i18next';
import { toast } from 'react-toastify';

export default function Merchants() {
    const { t } = useTranslation();
    const [merchants, setMerchants] = useState<any[]>([]);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchMerchants = async () => {
            try {
                const res = await axiosInstance.get('/merchants');
                // Обробка, якщо бекенд повертає об'єкт з полем content або просто масив
                setMerchants(res.data?.content || res.data || []);
            } catch {
                toast.error(t('merchants.error_load', 'Помилка завантаження продавців'));
            }
        };
        fetchMerchants();
    }, [t]);

    return (
        <div style={{ color: '#e0e0e0', fontFamily: 'sans-serif', maxWidth: '800px', margin: '0 auto', padding: '20px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <h1 style={{ margin: 0, fontSize: '28px' }}>{t('merchants.title', 'Продавці')}</h1>
                <button 
                    onClick={() => navigate('/merchants/new')} 
                    style={{ padding: '10px 20px', background: '#4CAF50', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}
                >
                    {t('merchants.new_title', 'Новий продавець')}
                </button>
            </div>

            <div style={{ display: 'grid', gap: '10px' }}>
                {merchants.map(m => (
                    <div 
                        key={m.id} 
                        onClick={() => navigate(`/merchants/edit/${m.id}`)}
                        style={{ 
                            border: '1px solid #333', 
                            padding: '15px', 
                            borderRadius: '8px', 
                            display: 'flex', 
                            alignItems: 'center', 
                            background: '#1e1e1e', 
                            cursor: 'pointer', 
                            width: '100%', 
                            boxSizing: 'border-box' 
                        }}
                    >
                        <div style={{ width: '45px', height: '45px', borderRadius: '8px', background: '#444', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '24px' }}>
                            {m.icon || '🏪'}
                        </div>
                        
                        <div style={{ marginLeft: '15px', flex: 1 }}>
                            <h3 style={{ margin: 0, color: '#fff', fontSize: '18px' }}>{m.name}</h3>
                        </div>

                        <div style={{ marginLeft: 'auto' }}>
                            {m.category ? (
                                <span style={{ 
                                    padding: '5px 12px', 
                                    borderRadius: '12px', 
                                    background: '#2c3e50', 
                                    fontSize: '12px', 
                                    color: '#3498db',
                                    fontWeight: 'bold',
                                    border: '1px solid #3498db'
                                }}>
                                    {m.category.title}
                                </span>
                            ) : (
                                <span style={{ fontSize: '12px', color: '#555', fontStyle: 'italic' }}>
                                    {t('merchants.no_category', 'Без категорії')}
                                </span>
                            )}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}