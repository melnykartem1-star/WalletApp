import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { axiosInstance } from '../../api/axiosInstance';
import { useTranslation } from 'react-i18next';

export default function Categories() {
    const { t } = useTranslation();
    const [categories, setCategories] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        fetchCategories();
    }, []);

    const fetchCategories = async () => {
        try {
            setLoading(true);
            const response = await axiosInstance.get('/categories');
            const data = response.data?.content || response.data || [];
            setCategories(data);
        } catch (error) {
            console.error("Помилка завантаження", error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div style={{ color: '#fff', textAlign: 'center', marginTop: '50px' }}>{t('common.loading', 'Завантаження...')}</div>;

    return (
        <div style={{ color: '#e0e0e0', fontFamily: 'sans-serif', maxWidth: '800px', margin: '0 auto', padding: '20px' }}>
            
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <h1 style={{ margin: 0, fontSize: '28px' }}>{t('categories.title', 'Категорії')}</h1>
                <button 
                    onClick={() => navigate('/categories/new')} 
                    style={{ padding: '10px 20px', background: '#4CAF50', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', flexShrink: 0 }}
                >
                  {t('categories.new_title', 'Нова категорія')}
                </button>
            </div>

            <div style={{ display: 'grid', gap: '10px' }}>
                {Array.isArray(categories) && categories.length > 0 ? (
                    categories.map((c) => (
                        <div 
                            key={c.id || Math.random()} 
                            onClick={() => navigate(`/categories/edit/${c.id}`)}
                            style={{ 
                                border: '1px solid #333', 
                                padding: '15px', 
                                borderRadius: '8px', 
                                display: 'flex', 
                                alignItems: 'flex-start', 
                                background: '#1e1e1e', 
                                cursor: 'pointer',
                                width: '100%', 
                                boxSizing: 'border-box',
                                overflow: 'hidden' /* Обрізає все, що спробує вилізти за межі рамки */
                            }}
                        >
                            {/* Іконка */}
                            <div style={{ width: '45px', height: '45px', borderRadius: '8px', background: c.color || '#444', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '24px', flexShrink: 0 }}>
                                {c.icon || '📌'}
                            </div>
                            
                            {/* Текстовий блок */}
                            <div style={{ marginLeft: '15px', flex: 1, minWidth: 0, display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
                                <h3 style={{ margin: '0 0 5px 0', color: '#fff', fontSize: '18px', textAlign: 'left', wordBreak: 'break-word' }}>
                                    {c.title}
                                </h3>
                                
                                <p style={{ 
                                    margin: 0, 
                                    color: '#aaa', 
                                    fontSize: '14px', 
                                    lineHeight: '1.5', 
                                    whiteSpace: 'normal', 
                                    wordBreak: 'break-all', 
                                    textAlign: 'left',
                                    width: '100%'
                                }}>
                                    {c.description || ''}
                                </p>
                            </div>
                        </div>
                    ))
                ) : (
                    <p style={{ color: '#aaa', textAlign: 'center' }}>{t('categories.no_categories', 'Категорій немає.')}</p>
                )}
            </div>
        </div>
    );
}