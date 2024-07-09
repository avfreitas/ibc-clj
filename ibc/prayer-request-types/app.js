document.addEventListener('DOMContentLoaded', function () {
    const listPrayerRequestTypesButton = document.getElementById('list-prayer-request-types');
    const prayerRequestTypesList = document.getElementById('prayer-request-types-list');
    const addPrayerRequestTypeForm = document.getElementById('add-prayer-request-type-form');
    const deletePrayerRequestTypeForm = document.getElementById('delete-prayer-request-type-form');
    const checkPrayerIdForm = document.getElementById('check-prayer-id-form');
    const updatePrayerRequestTypeForm = document.getElementById('update-prayer-request-type-form');
    
    //const backendUrl = 'http://localhost:8888/ibc-clj';  
    const backendUrl = 'https://www.dev-app.qualitsys.net/ibc-clj';  
    
    //-----------------------------------------------------------------------------
    //---  Função para listar os prayer_request_types
    //-----------------------------------------------------------------------------
    async function listPrayerRequestTypes() {
        try {
            const response = await fetch(`${backendUrl}/list-prayer-request-types`, {
                method: 'GET'
            });
            if (!response.ok) {
                throw new Error(`listPrayerRequestTypes - Erro ao listar prayer request types: ${response.statusText}`);
            }
            const data = await response.json();
            prayerRequestTypesList.innerHTML = '';
            data.forEach(prayerRequestType => {
                const li = document.createElement('li');
                li.textContent = `${prayerRequestType['prayer_request_types/id']}: ${prayerRequestType['prayer_request_types/description']}`;
                prayerRequestTypesList.appendChild(li);
            });
        } catch (error) {
            console.error('Erro ao listar prayer request types:', error);
            alert(`listPrayerRequestTypes - Erro ao listar prayer request types: ${error.message}`);
        }
    }

    //------------------------------------------------------------------------------------------
    //---- Função para verificar se o prayer request type existe e obter seus dados
    //------------------------------------------------------------------------------------------
    async function checkPrayerRequestTypeExists(id) {
        try {
            const response = await fetch(`${backendUrl}/list-prayer-request-types`, {
                method: 'GET'
            });
            if (!response.ok) {
                throw new Error(`checkPrayerRequestTypeExists - Erro em fetch de prayer request types: ${response.statusText}`);
            }
            const data = await response.json();
            return data.find(prayerRequestType => prayerRequestType['prayer_request_types/id'] === parseInt(id));
        } catch (error) {
            console.error('checkPrayerRequestTypeExists - Erro em fetch de prayer request types:', error);
            return null;
        }
    }
    //-------------------------------------------------------------------------------------------
    //--- Função para adicionar um novo prayer_request_type  
    //-------------------------------------------------------------------------------------------
    async function addPrayerRequestType(event) {
        event.preventDefault();
        try {
            const formData = new FormData(addPrayerRequestTypeForm);
            const data = {};

            formData.forEach((value, key) => {
                data[key] = value.trim() === "" ? null : value.trim();
            });

            if (!data.id || !data.description) {
                alert('Por favor, preencha todos os campos obrigatórios do formulário.');
                return;
            }

            const jsonData = JSON.stringify(data);

            const response = await fetch(`${backendUrl}/ins-prayer-request-type`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: jsonData
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Erro ao adicionar prayer request type: ${response.statusText}, ${errorText}`);
            }

            const result = await response.json();
            alert(result.message || result.error);
        } catch (error) {
            console.error('Erro ao adicionar prayer request type:', error);
            alert(`Erro ao adicionar prayer request type: ${error.message}`);
        }
    }

    //-------------------------------------------------------------------------------------
    //----   Função para deletar um prayer_request_type
    //-------------------------------------------------------------------------------------
    async function deletePrayerRequestType(event) {
        event.preventDefault();
        try {
            const formData = new FormData(deletePrayerRequestTypeForm);
            const data = {};

            formData.forEach((value, key) => {
                data[key] = value.trim() === "" ? null : value.trim();
            });

            const response = await fetch(`${backendUrl}/del-prayer-request-type`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Erro ao deletar prayer request type: ${response.statusText}, ${errorText}`);
            }
            const result = await response.json();
            alert(result.message || result.error);
        } catch (error) {
            console.error('Erro ao deletar prayer request type:', error);
            alert(`Erro ao deletar prayer request type: ${error.message}`);
        }
    }

    //------------------------------------------------------------------------------------------------
    //---- Função para mostrar os campos de atualização com dados preenchidos
    //------------------------------------------------------------------------------------------------
    function showUpdatePrayerFields(prayerRequestType) {
        document.getElementById('update-prayer-id').value = prayerRequestType['prayer_request_types/id'];
        document.getElementById('update-prayer-description').value = prayerRequestType['prayer_request_types/description'];
        document.getElementById('update-prayer-updated_by').value = prayerRequestType['prayer_request_types/updated_by'];
        updatePrayerRequestTypeForm.style.display = 'block';
    }

    //----------------------------------------------------------------------------------------------
    //---- Função para checar se o ID existe e mostrar os campos de atualização
    //----------------------------------------------------------------------------------------------
    async function checkPrayerId(event) {
        event.preventDefault();
        const id = document.getElementById('check-prayer-id').value.trim();
        if (!id) {
            alert('Por favor, insira o ID.');
            return;
        }
        const prayerRequestType = await checkPrayerRequestTypeExists(id);
        if (!prayerRequestType) {
            alert(`Prayer request type com ID ${id} não encontrado.`);
            return;
        }
        showUpdatePrayerFields(prayerRequestType);
    }

    //-----------------------------------------------------------------------------------------------
    //----  Função para atualizar um tipo de pedido de oração
    //-----------------------------------------------------------------------------------------------
    // Função para atualizar um tipo de pedido de oração
async function updatePrayerRequestType(event) {
    console.log("Função updatePrayerRequestType em execução!");
    event.preventDefault();
    try {
        const formData = new FormData(updatePrayerRequestTypeForm);
        const data = {};

        formData.forEach((value, key) => {
            data[key] = value.trim() === "" ? null : value.trim();
        });

        console.log('Dados a serem enviados ao backend:', data);

        const response = await fetch(`${backendUrl}/upd-prayer-request-type`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        console.log('Resposta da solicitação:', response);

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to update prayer request type: ${response.statusText}, ${errorText}`);
        }

        const result = await response.json();
        console.log('Resultado da atualização:', result);
        alert(result.message || result.error);
    } catch (error) {
        console.error('Error updating prayer request type:', error);
        alert(`Error updating prayer request type: ${error.message}`);
    }
}

    //--------------------------------------------------------------------------------------------
    //----  Adiciona eventos aos botões e formulários
    //--------------------------------------------------------------------------------------------
    listPrayerRequestTypesButton.addEventListener('click', listPrayerRequestTypes);
    addPrayerRequestTypeForm.addEventListener('submit', addPrayerRequestType);
    deletePrayerRequestTypeForm.addEventListener('submit', deletePrayerRequestType);
    checkPrayerIdForm.addEventListener('submit', checkPrayerId);
    updatePrayerRequestTypeForm.addEventListener('submit', updatePrayerRequestType);
});
//------------------------------------------------------------------------------------------------